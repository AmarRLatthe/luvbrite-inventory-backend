package com.luvbrite.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.luvbrite.jdbcutils.PacketExtDTOMapper;
import com.luvbrite.model.PacketExtDTO;
import com.luvbrite.model.PaginatedPackets;
import com.luvbrite.model.Pagination;
import com.luvbrite.model.PaginationLogic;


@Repository
public class PacketRepositoryImpl implements IPacketRepository {

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

}
