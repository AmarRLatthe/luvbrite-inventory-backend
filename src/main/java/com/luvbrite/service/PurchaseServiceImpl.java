package com.luvbrite.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.luvbrite.model.PaginatedPurchase;
import com.luvbrite.model.Pagination;
import com.luvbrite.model.PaginationLogic;
import com.luvbrite.model.PurchaseDTO;
import com.luvbrite.repository.IPurchaseRepository;

@Service
//@Slf4j
public class PurchaseServiceImpl implements IPurchaseService {

	private Pagination pg;
	private final int itemsPerPage = 15;

	@Autowired
	private IPurchaseRepository iPurchaseRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public PurchaseDTO addPurchase(PurchaseDTO purchase) throws Exception {
		return iPurchaseRepository.addPurchase(purchase);
	}

	@Override
	public List<PurchaseDTO> getAllPurchases() throws Exception {

		return null;
	}



	public PaginatedPurchase getPurchases(String orderBy, 
			                              String sortDirection, 
			                              String productName, 
			                              String packetCode,
			                              Integer vendorId, 
			                              String startDate, 
			                              String endDate, 
			                              String source, 
			                              Integer productId, 
			                              Boolean adjustmentsOnly,
			                              Integer currentPage)  {

		
		int offset = 0;
		String caller = "";
		String qWHERE = "", 
				qOFFSET = "", 
				qLIMIT = " LIMIT " + itemsPerPage + " ", 
				qORDERBY = "ORDER BY id DESC";

		PaginationLogic pgl = null;

		if (source != null && source.equals("report"))
			caller = "report";

		if (vendorId != 0)
			qWHERE = " WHERE pi.vendor_id = " + vendorId + " ";

		if (productName != null && !productName.trim().equals("")) {
			if (qWHERE.equals(""))
				qWHERE = " WHERE p.product_name ~* '.*" + productName.replace("'", "''") + ".*' ";
			else
				qWHERE += " AND p.product_name ~* '.*" + productName.replace("'", "''") + ".*' ";
		}

		if (packetCode != null && !packetCode.trim().equals("")) {
			if (qWHERE.equals(""))
				qWHERE = " WHERE pi.id IN (SELECT DISTINCT(purchase_id) "
						+ "FROM packet_inventory WHERE packet_code ~* '.*" + packetCode + ".*') ";
			else
				qWHERE += " AND pi.id IN (SELECT DISTINCT(purchase_id) "
						+ "FROM packet_inventory WHERE packet_code ~* '.*" + packetCode + ".*') ";
		}

		if (orderBy != null && !orderBy.trim().equals("")) {
			switch (orderBy) {
			case "product":
				qORDERBY = "ORDER BY p.product_name " + sortDirection + " ";
				break;

			case "condition":
				qORDERBY = "ORDER BY pi.growth_condition " + sortDirection + " ";
				break;

			case "qty":
				qORDERBY = "ORDER BY pi.quantity " + sortDirection + " ";
				break;

			case "weight":
				qORDERBY = "ORDER BY pi.weight_in_grams " + sortDirection + " ";
				break;

			case "price":
				qORDERBY = "ORDER BY pi.unit_price " + sortDirection + " ";
				break;

			case "vendor":
				qORDERBY = "ORDER BY v.vendor_name " + sortDirection + " ";
				break;

			case "date":
				qORDERBY = "ORDER BY pi.date_added " + sortDirection + " ";
				break;
			}
		}

		// Ignore old purchases, if its a generic listing
		if (qWHERE.equals(""))
			qWHERE = " WHERE pi.id > 204026 ";

		if (caller.equals("report")) {

			if (startDate == null || startDate.trim().equals("")) {

				return null;
			}

			if (endDate.trim().equals("")) {
				qWHERE = " WHERE pi.date_added >= to_date('" + startDate + "', 'MM/dd/YYYY')";
			} else {
				qWHERE = " WHERE pi.date_added >= to_date('" + startDate + "', 'MM/dd/YYYY') "
						+ "AND  pi.date_added < to_date('" + endDate + "', 'MM/dd/YYYY') + interval '1 day'";
			}

			if (productId != 0) {
				qWHERE += " AND p.id = " + productId + " ";
			}

			if (adjustmentsOnly) {
				qWHERE += " AND pi.growth_condition = 'Adjustment' ";
			}

			// Remove any LIMIT set for the query.
			qLIMIT = "";

		} else {
			// Pagination logic is not required for report

			// currentPage = routine.getInteger(req.getParameter("cpage"));
			if (currentPage <= 0) {
				currentPage = 1;
			}

			StringBuilder countString = new StringBuilder();

			countString.append("SELECT COUNT(*) ")
			           .append("FROM purchase_inventory pi  ")
					   .append("JOIN  products p ON p.id = pi.product_id ")
					   .append("JOIN  vendors v ON v.id = pi.vendor_id ").append(qWHERE);

			Integer totalPurchase = jdbcTemplate.queryForObject(countString.toString(), Integer.class);

			if (totalPurchase > 0) {
				pgl = new PaginationLogic(totalPurchase, itemsPerPage, currentPage);
			}

			pg = pgl.getPg();
			offset = pg.getOffset();
			if (offset > 0)
				qOFFSET = " OFFSET " + offset;
		}

		ArrayList<PurchaseDTO> purchases = new ArrayList<PurchaseDTO>();

		StringBuilder queryStringBuilder = new StringBuilder();
		queryStringBuilder.append("SELECT pi.*, p.product_name, p.category_id, v.vendor_name, ")
				          .append("TO_CHAR(pi.date_added, 'MM/dd/yyyy') as date ")
				          .append("FROM purchase_inventory pi ")
				          .append("JOIN  products p ON p.id = pi.product_id ")
				          .append("JOIN  vendors v ON v.id = pi.vendor_id ")
				          .append(qWHERE)
				          .append(qORDERBY)
				          .append(qLIMIT)
				          .append(qOFFSET);

		jdbcTemplate.query(queryStringBuilder.toString(), new RowCallbackHandler() {
			public void processRow(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					
					PurchaseDTO purchase = new PurchaseDTO();

					purchase.setId(resultSet.getInt(1));
					purchase.setProductId(resultSet.getInt(2));
					purchase.setGrowthCondition(resultSet.getString(3));
					purchase.setQuantity(resultSet.getInt(4));
					purchase.setWeightInGrams(resultSet.getDouble(5));
					purchase.setUnitPrice(resultSet.getDouble(6));
					purchase.setVendorId(resultSet.getInt(7));
					purchase.setOperatorComments(resultSet.getString(8));
					purchase.setPurchaseCode(resultSet.getString(10));
					purchase.setProductName(resultSet.getString("product_name"));
					purchase.setVendorName(resultSet.getString("vendor_name"));
					purchase.setDateAdded(resultSet.getString("date"));
					purchase.setCategoryId(resultSet.getInt("category_id"));
					
					purchases.add(purchase);
					
				}
			}

		});

		PaginatedPurchase	 pgPurchase = new PaginatedPurchase(pg, purchases);

		return pgPurchase;
	}


}
