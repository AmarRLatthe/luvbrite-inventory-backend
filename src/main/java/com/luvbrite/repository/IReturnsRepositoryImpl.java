package com.luvbrite.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.luvbrite.model.*;
import com.luvbrite.service.MasterInventoryService;
import com.luvbrite.service.Tracker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class IReturnsRepositoryImpl implements IReturnsRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MasterInventoryService masterInventoryService;

    @Autowired
    Tracker track;

    private Pagination pg;
    private int itemsPerPage = 15;

    @Override
    public List<String> listReturnReasons(Integer shopId) {

        List<String> reasons = new ArrayList<String>();

        StringBuffer listReasonsQry = new StringBuffer();
        listReasonsQry
                .append("SELECT DISTINCT(reason) FROM returns_detail ")
                .append("WHERE shop_id = ? ")
                .append("GROUP BY reason HAVING COUNT(id) > 5");


        reasons = jdbcTemplate.query(listReasonsQry.toString(), new Object[]{shopId}, new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {

                return rs.getString("reason");
            }

        });

        return reasons;
    }

    @Override
    public PaginatedReturns listReturns(Integer currentPage, Integer shopId) throws Exception {


        java.lang.String qWHERE = "";
        java.lang.String qOFFSET = "";
        java.lang.String qLIMIT = " LIMIT " + itemsPerPage + " ";
        java.lang.String qORDERBY = "ORDER BY rd.id DESC";

        int offset = 0;

        PaginationLogic pgl = null;


        List<ReturnsDTO> returns = new ArrayList<ReturnsDTO>();

        if (currentPage <= 0) {
            currentPage = 1;
        }

        StringBuffer listReturnsQry = new StringBuffer();
        listReturnsQry.append("SELECT COUNT(*) ")
                .append("FROM returns_detail")
                .append("WHERE shop_id = ?");

        Integer totalReturnsForShop = jdbcTemplate.queryForObject(listReturnsQry.toString(), new Object[shopId], Integer.class);

        //System.out.println("ListReturns - " + countString);
        if (totalReturnsForShop > 0) {
            pgl = new PaginationLogic(totalReturnsForShop, itemsPerPage, currentPage);
        }


        pg = pgl.getPg();
        offset = pg.getOffset();
        if (offset > 0) {
            qOFFSET = " OFFSET " + offset;
        }

        StringBuffer returnedPacketsQry = new StringBuffer();

        returnedPacketsQry.append("SELECT rd.id, rd.reason, TO_CHAR(rd.date_added, 'MM/dd/yyyy'), ")
                .append("p.product_name, pi.packet_code ")
                .append("FROM returns_detail rd ")
                .append("JOIN packet_inventory pi ON pi.returns_detail_id = rd.id ")
                .append("JOIN purchase_inventory pur ON pi.purchase_id = pur.id ")
                .append("JOIN products p ON p.id = pur.product_id ")
                .append(qWHERE)
                .append(" AND rd.shop_id = ?")
                .append(qORDERBY)
                .append(qLIMIT)
                .append(qOFFSET);

        returns = jdbcTemplate.query(returnedPacketsQry.toString(), new Object[shopId], new RowMapper<ReturnsDTO>() {
            @Override
            public ReturnsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReturnsDTO returnsDTO = new ReturnsDTO();
                returnsDTO.setId(rs.getInt(1));
                returnsDTO.setReason(rs.getString(2));
                returnsDTO.setDateAdded(rs.getString(3));
                returnsDTO.setProductName(rs.getString(4));
                returnsDTO.setPacketCode(rs.getString(5));

                return returnsDTO;
            }
        });
        //System.out.println("ListReturns " + queryString);


        return new PaginatedReturns(pg, returns);


    }

    @Override
    public int deleteReturn(Integer returnId, Integer shopId, Integer operatorId) throws Exception {

        /***
          * return -1: no valid packets found ;
         *  return -2: maybe already deleted ;
         *  return -3: if packetId is 0 ;
         *  return 0:  return could not be deleted successfully;
         *  return 1:  returns deleted successfully;
         * */

        int packetId = 0;
        String packetCode = "";

        //Check if the packet code is valid.
        StringBuffer checkIfPacketCodeIsValidQry = new StringBuffer();
        checkIfPacketCodeIsValidQry.append("SELECT id, packet_code FROM packet_inventory")
                .append(" WHERE ")
                .append("returns_detail_id = ?")
                .append("AND")
                .append("shop_id = ?");

        PacketInventoryDTO packetInventoryDTO = jdbcTemplate.queryForObject(checkIfPacketCodeIsValidQry.toString(), new Object[]{returnId, shopId}, new RowMapper<PacketInventoryDTO>() {
            @Override
            public PacketInventoryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                PacketInventoryDTO dto = new PacketInventoryDTO();

                dto.setId(rs.getInt("id"));
                dto.setPacketCode(rs.getString("packet_code"));

                return dto;
            }

        });

        if (packetInventoryDTO != null) {
            packetId = packetInventoryDTO.getId();
            packetCode = packetInventoryDTO.getPacketCode();
        } else {
            log.error("No valid packets found.");
            return -1;
        }


        if (packetId != 0) {

            int deletedRows = 0;
            //Update packet_inventory
            StringBuffer updatePacketInvQry = new StringBuffer();

            updatePacketInvQry
                    .append("UPDATE packet_inventory SET returns_detail_id =0")
                    .append("WHERE id = ? ")
                    .append("AND")
                    .append("shop_id = ?");

            int updatedRows = jdbcTemplate.update(updatePacketInvQry.toString(), new Object[]{packetId, shopId});

            if (updatedRows != 0) {

                //Delete from returns_detail
                StringBuffer deleteReturnIdFromReturnDetailQry = new StringBuffer();

                deleteReturnIdFromReturnDetailQry
                        .append("DELETE FROM returns_detail ")
                        .append("WHERE id = ? ")
                        .append("AND")
                        .append("shop_id = ?");

                deletedRows = jdbcTemplate.update(deleteReturnIdFromReturnDetailQry.toString(), new Object[]{packetId, shopId});

                if (deletedRows == 0) {
                    log.info("returns info could not be deleted from returns_detail table for packetCode {} , whose returnId is {} ", packetCode, returnId);
                    return 0;
                }

                //TODO:Integrate Master Service

                if (StringUtils.isNotBlank(packetCode)) {
                    StringBuffer qryProductIdQry = new StringBuffer();

                    qryProductIdQry
                            .append("SELECT purinv.product_id FROM packet_inventory pktinv ")
                            .append("JOIN purchase_inventory purinv ON purinv.id = pktinv.purchase_id ")
                            .append("WHERE pktinv.packet_code = ? ")
                            .append("AND ")
                            .append("shop_id = ?");

                    int productId = jdbcTemplate.queryForObject(qryProductIdQry.toString(),
                            new Object[]{},
                            Integer.class);

                    if (productId != 0) {
                        masterInventoryService.updateProducts(productId, shopId);
                        log.info("Successfully updated master inventory service");
                    }

                }


                ChangeTrackerDTO ct = new ChangeTrackerDTO();
                ct.setActionDetails("Return deleted for package - " + packetCode);
                ct.setActionType("update");
                ct.setActionOn("packet");
                ct.setItemId(packetId);
                ct.setOperatorId(operatorId);

                track.track(ct);

                return 1;

            } else {
                log.error("Could not mark packet {} as  not returned ", packetCode);
                return -3;
            }


        } else {
            log.error("No corresponding packetId found in database for returnId : " + returnId + " for shopId :" + shopId);
            return -2;
        }



    }


}
