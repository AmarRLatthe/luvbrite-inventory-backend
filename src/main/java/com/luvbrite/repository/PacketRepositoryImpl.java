package com.luvbrite.repository;

import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.luvbrite.jdbcutils.PacketExtDTOMapper;
import com.luvbrite.model.BarcodeSequenceDTO;
import com.luvbrite.model.BulkPacketsCreation;
import com.luvbrite.model.SinglePacketDTO;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.luvbrite.model.PacketExtDTO;
import com.luvbrite.model.PaginatedPackets;
import com.luvbrite.model.Pagination;
import com.luvbrite.model.PaginationLogic;



@Repository
@NoArgsConstructor
@Slf4j
public class PacketRepositoryImpl implements IPacketRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private Pagination pg;
	private int itemsPerPage = 15;

	@Override
	public PaginatedPackets listPackets(Integer purchaseId, Integer salesId, Integer shopId, Boolean notSold,
			Boolean sold, Boolean allPackets, String orderBy, String sortDirection, String packetCode, String allmisc,
			Integer currentPage) throws Exception {
		String qWHERE = "", qOFFSET = "", qLIMIT = " LIMIT " + itemsPerPage + " ", qORDERBY = "ORDER BY pi.id DESC";

		int offset = 0;
		List<PacketExtDTO> packetList = new ArrayList<PacketExtDTO>();
		PaginationLogic pgl = null;

		try {



			if (allPackets) {
				itemsPerPage = 999; /* Setting high value to avoid pagination */
				qLIMIT = "";
			}

			if ((orderBy != null) && !orderBy.trim().equals("")) {
				switch (orderBy) {
				case "product":
					qORDERBY = "ORDER BY p.product_name " + sortDirection + " ";
					break;

				case "weight":
					qORDERBY = "ORDER BY pi.weight_in_grams " + sortDirection + " ";
					break;

				case "price":
					qORDERBY = "ORDER BY pi.marked_price " + sortDirection + " ";
					break;

				case "sprice":
					qORDERBY = "ORDER BY pi.selling_price " + sortDirection + " ";
					break;

				case "shop":
					qORDERBY = "ORDER BY sp.shop_name " + sortDirection + " ";
					break;

				case "date":
					qORDERBY = "ORDER BY pi.date_sold " + sortDirection + " ";
					break;
				}
			}

			if (purchaseId != 0) {
				qWHERE = " WHERE pi.purchase_id = " + purchaseId + " ";
			}

			if (allmisc != null) {
				qWHERE = " WHERE pi.purchase_id IN (202000, 202316) ";
			}

			if (salesId > 0) {

				/**
				 * We don't need pagination to work here, so we remove the LIMIT and set
				 * itemsPerPage to a higher value
				 */
				itemsPerPage = 999;
				qLIMIT = "";

				if (qWHERE.equals("")) {
					qWHERE = " WHERE pi.sales_id = " + salesId + " ";
				} else {
					qWHERE += " AND pi.sales_id = " + salesId + " ";
				}
			}

			if (shopId > 0) {
				if (qWHERE.equals("")) {
					qWHERE = " WHERE pi.shop_id = " + shopId + " ";
				} else {
					qWHERE += " AND pi.shop_id = " + shopId + " ";
				}
			}

			if (notSold) {
				if (qWHERE.equals("")) {
					qWHERE = " WHERE pi.sales_id = 0 ";
				} else {
					qWHERE += " AND pi.sales_id = 0 ";
				}
			} else if (sold) {
				if (qWHERE.equals("")) {
					qWHERE = " WHERE pi.sales_id <> 0 ";
				} else {
					qWHERE += " AND pi.sales_id <> 0 ";
				}

			}

			/* NOT USED ANYMORE. THERE IS NO MORE CONCEPT OF BOXES */
			// else if(inShop){

			/**
			 * In shop means it has not been assigned to a box and it has not been sold.
			 * Ideally sales happens through driver, so it's enough to check is box_id = 0.
			 * But I am just being cautious!
			 **/
			/*
			 * if(qWHERE.equals("")) qWHERE = " WHERE pi.box_id = 0 AND pi.sales_id = 0 ";
			 * else qWHERE += " AND pi.box_id = 0 AND pi.sales_id = 0 ";
			 *
			 * }
			 */

			/**
			 * inShops check should be only done, if a specific shop is not selected yet.
			 **/
			/*
			 * else if(inShops && shopId <=0 ){ if(qWHERE.equals("")) qWHERE =
			 * " WHERE pi.shop_id <> 0 "; else qWHERE += " AND pi.shop_id <> 0 ";
			 *
			 * }
			 */

			if ((packetCode != null) && !packetCode.trim().equals("")) {
				if (salesId == -1) {
					/**
					 * We don't want to show sold packets here.
					 */
					if (qWHERE.equals("")) {
						qWHERE = " WHERE pi.sales_id = 0 AND packet_code ~* '.*" + packetCode + ".*' ";
					} else {
						qWHERE += " AND  pi.sales_id = 0 AND packet_code ~* '.*" + packetCode + ".*' ";
					}
				}

				else {
					if (qWHERE.equals("")) {
						qWHERE = " WHERE packet_code ~* '.*" + packetCode + ".*' ";
					} else {
						qWHERE += " AND  packet_code ~* '.*" + packetCode + ".*' ";
					}
				}
			}

			if (currentPage <= 0) {
				currentPage = 1;
			}

			StringBuffer countString = new StringBuffer();
			countString
			.append("SELECT COUNT(*) ")
			.append("FROM packet_inventory pi ")
			.append("JOIN purchase_inventory pur ON pi.purchase_id = pur.id ")
			.append("JOIN products p ON p.id = pur.product_id ")
			.append("JOIN shops sp ON sp.id = pi.shop_id ")
			.append(qWHERE);


			System.out.println("PacketList query "+countString.toString());
			int totalCount = jdbcTemplate.queryForObject(countString.toString(), Integer.class);

			System.out.println("ListPackets - " + totalCount);
			if (totalCount > 0) {
				pgl = new PaginationLogic(totalCount, itemsPerPage, currentPage);
			}

			pg = pgl.getPg();
			offset = pg.getOffset();
			if (offset > 0) {
				qOFFSET = " OFFSET " + offset;
			}

			StringBuffer query = new StringBuffer();
			query.append("SELECT pi.*, ")
			.append("TO_CHAR(pi.date_added, 'MM/dd/yyyy') AS add_date, TO_CHAR(pi.date_sold, 'MM/dd/yyyy') AS sold_date, ")
			.append("p.id AS product_id, p.product_name, ")
			.append("sp.shop_name, ")
			.append("COALESCE(rd.reason,'') AS return_reason ")
			.append("FROM packet_inventory pi ")
			.append("JOIN purchase_inventory pur ON pi.purchase_id = pur.id ")
			.append("JOIN products p ON p.id = pur.product_id ")
			.append("JOIN shops sp ON sp.id = pi.shop_id ")
			.append("LEFT JOIN returns_detail rd ON rd.id = pi.returns_detail_id ")
			.append(qWHERE)
			.append(qORDERBY)
			.append(qLIMIT)
			.append(qOFFSET);

			// System.out.println("ListPackets " + queryString);
			packetList = jdbcTemplate.query(query.toString(), new PacketExtDTOMapper());
		} catch (Exception e) {

			e.printStackTrace();

			throw e;
		}

		return new PaginatedPackets(pg, packetList);
	}

//	@Autowired
//	private MasterInventoryService inventoryService;
	
	@Transactional
	@Override
	public int createSinglePkt(SinglePacketDTO singlePacket) {
		try {
			StringBuilder qry = new StringBuilder();
			qry.append(" INSERT INTO  ")
				.append(" packet_inventory ( purchase_id, packet_code, weight_in_grams, marked_price ,shop_id)  ")
				.append(" VALUES (?,?,?,?,?) RETURNING id"); 
			 Integer pktId =  jdbcTemplate.queryForObject(qry.toString(), new Object[] {
						 singlePacket.getPurchaseId(),
						 singlePacket.getSku(),
						 singlePacket.getWeight(),
						 singlePacket.getPrice(),
						 singlePacket.getShopId()
			 			},
					 Integer.class);
			 if(pktId!=null && pktId>0) {
//				update =updateTrackerDetails(singlePacket.getOperatorId(),pktId,"insert","packet","New packet with id:" + pktId + ": created");
//				if(update>0) {
//					int prodId = getProdIdByPurchaseId(singlePacket.getPurchaseId());
//				}
//				 inventoryService.
			 }
			return pktId;
		} catch (Exception e) {
			log.error("Message is {} and Exception exception is {}",e.getMessage(),e);
			return -1;
		}

	}

	@Transactional
	@Override
	public int updatePktById(Integer id, SinglePacketDTO singlePacket) {
		try {
			StringBuilder qry = new StringBuilder();
			qry.append(" UPDATE packet_inventory  ")
				.append(" SET ")
				.append(" packet_code=?, ")
				.append(" weight_in_grams=?, ")
				.append(" marked_price=?  ")
				.append(" WHERE ")
				.append("  id=? ");
			
			return jdbcTemplate.update(qry.toString(),singlePacket.getSku(),singlePacket.getWeight(),singlePacket.getPrice(),id);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return -1;
		}
		
	}

	@Transactional
	@Override
	public int createBulkPackets(BulkPacketsCreation packets) {
		try {
			BarcodeSequenceDTO barcodeSequenceDTO = getBarcodeInfo();
			long nextVal = createBulkPktsNRtrnNxtVl(barcodeSequenceDTO, packets);
			int updateNextVal = updateBarcodeNextValueById(barcodeSequenceDTO.getId() , nextVal);
			return updateNextVal;
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}

	}

	private int updateBarcodeNextValueById(Integer id, long nextVal) {
		
		return jdbcTemplate.update("UPDATE barcode_sequence SET next_val = ? WHERE id = ?",nextVal,id);
	}

	private long createBulkPktsNRtrnNxtVl(BarcodeSequenceDTO barcodeSequenceDTO, BulkPacketsCreation packets) {
		StringBuilder createPktQry = new StringBuilder();
		createPktQry.append(" INSERT INTO ")
					.append(" packet_inventory ")
					.append(" (purchase_id, packet_code, weight_in_grams, marked_price, shop_id) ")
					.append(" VALUES ")
					.append(" (?,?,?,?, ?)  ");
		   jdbcTemplate.batchUpdate(createPktQry.toString(),
				 	new BatchPreparedStatementSetter() {
						Long skuNum = barcodeSequenceDTO.getNextVal();
						String prefix = barcodeSequenceDTO.getPrefix();
						@Override
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setInt(1, packets.getPurchaseId());
							ps.setString(2, prefix+skuNum.toString());
							ps.setDouble(3,packets.getWeight());
							ps.setDouble(4,packets.getPrice());
							ps.setInt(5, packets.getShopId());
							skuNum++;
							barcodeSequenceDTO.setNextVal(skuNum);
						}
						
						@Override
						public int getBatchSize() {
							return packets.getTotalPackets();
						}
					});
		 
		return barcodeSequenceDTO.getNextVal();
	}

	private BarcodeSequenceDTO getBarcodeInfo() {
		return  jdbcTemplate.queryForObject("SELECT barcode_prefix, id, next_val FROM barcode_sequence WHERE avery_template = ?", new Object[] {"dymo30346"},new RowMapper<BarcodeSequenceDTO>() {

			@Override
			public BarcodeSequenceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				BarcodeSequenceDTO  dto = new BarcodeSequenceDTO();
				dto.setId(rs.getInt("id"));
				dto.setNextVal(rs.getLong("next_val"));
				dto.setPrefix(rs.getString("barcode_prefix"));
				
				return dto;
			}
		});
	}

	@Override
	public int updatePktsByPriceNWeightNPurchaseId(Double price, Double weight, Integer purchaseId) {
		try {
			StringBuilder qry = new StringBuilder();
				qry.append(" UPDATE packet_inventory ")
					.append(" SET marked_price=? ")
					.append(" WHERE purchase_id=? ")
					.append(" AND weight_in_grams=? ")
					.append(" AND sales_id = 0 ");
				
			return jdbcTemplate.update(qry.toString(), price,purchaseId,weight);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return -1;
		}

	}

	@Override
	public boolean isAvailPacketBySKU(String sku) {
		try {
			int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM packet_inventory WHERE packet_code= ?",Integer.class,sku); 
			if(count>0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return false;
		}
				
	}
}
