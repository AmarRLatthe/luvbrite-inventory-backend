package com.luvbrite.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


import com.luvbrite.jdbcUtils.ProductCountAndWeightOfPurchaseMapper;
import com.luvbrite.jdbcUtils.ProductDetailsDTOMapper;
import com.luvbrite.jdbcUtils.ReturnedAndAdjustProductMapper;
import com.luvbrite.model.AdjustedAndReturnedDTO;
import com.luvbrite.model.ProductCountAndWeightOfPurchase;
import com.luvbrite.model.ProductDetailsDTO;
import com.luvbrite.model.PurchaseDTO;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MasterInventoryService {

	@Autowired
	ProductsAvailableTableUpdateService updateProductAvailableService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean updateProductsAvailable(Integer salesID, Integer shopId) {
		List<Integer> listOfDistinctProdIds = new ArrayList<Integer>();

		int category_id = 0;
		int tot_returnd = 0;
		int tot_adjusted = 0;
		double totpurchase_qty = 0d;
		double totpackets_qty = 0d;
		double totsold_qty = 0d;
		double totpurchase_wt = 0d;
		double totpacked_wt = 0d;
		double totsold_wt = 0d;
		String strain_name = "";
		ProductDetailsDTO childProductDetailsDTO = null;
		try {
			ArrayList<ProductDetailsDTO> productDetailsList = new ArrayList<ProductDetailsDTO>();

			StringBuffer sql_distinctPurchaseIdsForSalesId = new StringBuffer(); 
			sql_distinctPurchaseIdsForSalesId.append("SELECT DISTINCT purchase_id  FROM packet_inventory")
			.append("WHERE sales_id = ?");
			//.append(" AND shop_id = ?");
			
			
			List<Integer> distinctPurchaseIdsForSalesIds = null;
			distinctPurchaseIdsForSalesIds = jdbcTemplate.queryForList(sql_distinctPurchaseIdsForSalesId.toString(),
					new Object[] { salesID,shopId }, Integer.class);

			for (Integer salesPurchaseId : distinctPurchaseIdsForSalesIds) {
				String sql_productForPurchaseId = "SELECT product_id FROM purchase_inventory WHERE id = ?";

				Integer productId = jdbcTemplate.queryForObject(sql_productForPurchaseId,
						new Object[] { salesPurchaseId }, Integer.class);

				if (productId > 0) {

					if (listOfDistinctProdIds.contains(productId)) {
						continue;
					}

					listOfDistinctProdIds.add(productId);

					String sql_listOfPurchaseIdsForProd = "SELECT id FROM purchase_inventory WHERE product_id =  "
							+ productId + " AND id > 204026  ";

					List<Integer> listOfPurchaseIdsForProducts = jdbcTemplate.queryForList(sql_listOfPurchaseIdsForProd,
							null, Integer.class);

					
					for (Integer productPurchaseID : listOfPurchaseIdsForProducts) {

						StringBuffer qryBuffer = new StringBuffer();

						qryBuffer.append(
								"SELECT pi.growth_condition, pi.quantity, (pi.quantity*pi.weight_in_grams) AS purchased, ")
								.append("to_char(pi.date_added, 'MM/dd/YYYY') AS date_purchased,")
								.append("p.product_name, ")
								.append("v.vendor_name, ")
								.append("c.category_name, ")
								.append("s.strain_name, ")
								.append("s.id AS strainId,")
								.append("c.id AS categoryId ")
								.append("FROM purchase_inventory pi ")
								.append("JOIN products p on pi.product_id = p.id  ")
								.append("JOIN vendors v on pi.vendor_id = v.id  ")
								.append("JOIN categories c on p.category_id = c.id ")
								.append("JOIN strains s on p.strain_id = s.id ")
								.append("WHERE pi.id = ?");
								

						 childProductDetailsDTO = (ProductDetailsDTO) jdbcTemplate.queryForObject(
								qryBuffer.toString(), new Object[] { productPurchaseID },
								new ProductDetailsDTOMapper());

						if (childProductDetailsDTO.getCategoryName().equalsIgnoreCase("Flower")) {
						}

						int soldCount = 0;

						totpurchase_qty = childProductDetailsDTO.getTotal_purchase_qty() + totpurchase_qty;

						totpurchase_qty = childProductDetailsDTO.getTotal_purchase_qty() + totpurchase_qty;

						StringBuffer strTotalCountAndWeightOfAProduct = new StringBuffer();
						strTotalCountAndWeightOfAProduct.append("SELECT COUNT(id), SUM(weight_in_grams) ")
								.append("FROM packet_inventory ").append("WHERE purchase_id = ").append("?");

						ProductCountAndWeightOfPurchase productCountAndPurchase = jdbcTemplate.queryForObject(
								strTotalCountAndWeightOfAProduct.toString(), new Object[] { productPurchaseID },
								new ProductCountAndWeightOfPurchaseMapper());

						totpacked_wt = productCountAndPurchase.getTotalWeight() + totpacked_wt;
						totpackets_qty = productCountAndPurchase.getCount() + totpackets_qty;

						StringBuffer strTotalCountAndWeightOfAReturnedProduct = new StringBuffer();
						strTotalCountAndWeightOfAReturnedProduct.append("SELECT COUNT(id), SUM(weight_in_grams) ")
								.append("FROM packet_inventory ").append("AND returns_detail_id = 0 ")
								.append("AND purchase_id =  ").append("?");

						ProductCountAndWeightOfPurchase soldProductCountAndWeight = jdbcTemplate.queryForObject(
								strTotalCountAndWeightOfAProduct.toString(), new Object[] { productPurchaseID },
								new ProductCountAndWeightOfPurchaseMapper());

						soldCount = soldProductCountAndWeight.getCount();
						totsold_wt = totsold_wt + soldProductCountAndWeight.getTotalWeight();
						totsold_qty = totsold_qty + soldCount;

						StringBuffer adjustedAndReturned = new StringBuffer();
						adjustedAndReturned.append(" WITH ").append("returned AS  ")
								.append("(SELECT COUNT(*) AS tot_returned ").append(" FROM packet_inventory ")
								.append(" WHERE returns_detail_id > 0 AND purchase_id IN ").append("(").append("?")
								.append(")").append("), ").append("adjusted AS ")
								.append("(SELECT SUM(quantity) AS tot_adjusted ").append(" FROM purchase_inventory ")
								.append(" WHERE growth_condition = 'Adjustment' ").append(" AND id IN").append("(?)")
								.append(")").append("SELECT returned.*, adjusted.* FROM returned, adjusted ");

						List<AdjustedAndReturnedDTO> adjustedAndReturnedCount = jdbcTemplate
								.query(adjustedAndReturned.toString(), new ReturnedAndAdjustProductMapper());

						for (AdjustedAndReturnedDTO adjustedNdReturnd : adjustedAndReturnedCount) {
							tot_returnd = adjustedNdReturnd.getReturnedProducts() + tot_returnd;
							tot_adjusted = adjustedNdReturnd.getAdjustedProduct() + tot_adjusted;
						}
                          
						childProductDetailsDTO.setReturned(tot_returnd);
						childProductDetailsDTO.setReturned(tot_adjusted);
					}
                    
					
					if (childProductDetailsDTO != null) {
						 double totalremaining_qty = totpackets_qty + tot_adjusted - totsold_qty -tot_returnd;
						 childProductDetailsDTO.setTotal_remain_qty(totalremaining_qty);
						 
					}
				   
					productDetailsList.add(childProductDetailsDTO);
					
					//Updating products available table
					updateProductAvailableService.updateProductsAvailable(childProductDetailsDTO,shopId);
					
					category_id = 0;
					totpurchase_qty = 0d;
					totpackets_qty = 0d;
					totsold_qty = 0d;
					totpurchase_wt = 0d;
					totpacked_wt = 0d;
					totsold_wt = 0d;
					tot_adjusted = 0;
					tot_returnd = 0;
					strain_name = "";
				}

			}

		} catch (Exception e) {
            e.printStackTrace();
		}
		return false;
	}

	public boolean updateProductsAvailable(Integer salesID) {

		Connection tcon = null;
		ResultSet rs_disnctPurchaseIdsForSalesid = null, rs_productForPurchaseId = null,
				rs_listOfPurchaseForProd = null, rs = null, rs1 = null, rs_returndAndAdjsutd = null;

		PreparedStatement pst = null;
		int productID = 0;
		List<Integer> listOfDistnctProdIds = null;
		try {

			ArrayList<ProductDetailsDTO> productDetailsList = new ArrayList<ProductDetailsDTO>();

			// tcon = DBPoolAccess.sDBPoolAccess.getConnection();

			int strainid = 0;
			int category_id = 0;
			int tot_returnd = 0;
			int tot_adjusted = 0;
			double totpurchase_qty = 0d;
			double totpackets_qty = 0d;
			double totsold_qty = 0d;
			double totpurchase_wt = 0d;
			double totpacked_wt = 0d;
			double totsold_wt = 0d;
			String strain_name = "";

			Integer salesId = salesID;

			String sql_disnctPurchaseIdsForSalesid = "SELECT DISTINCT purchase_id  FROM packet_inventory WHERE sales_id ="
					+ salesId;
			rs_disnctPurchaseIdsForSalesid = tcon.createStatement().executeQuery(sql_disnctPurchaseIdsForSalesid);

			listOfDistnctProdIds = new ArrayList();

			while (rs_disnctPurchaseIdsForSalesid.next()) {
				Integer purchaseID = rs_disnctPurchaseIdsForSalesid.getInt("purchase_id");
				String sql_productForPurchaseId = "SELECT product_id FROM purchase_inventory WHERE id=" + purchaseID;
				rs_productForPurchaseId = tcon.createStatement().executeQuery(sql_productForPurchaseId);

				if (rs_productForPurchaseId.next()) {

					productID = rs_productForPurchaseId.getInt("product_id");

					if (listOfDistnctProdIds.contains((Integer) productID)) {
						continue;
					}
					listOfDistnctProdIds.add(productID);

					String sql_listOfPurchaseForProd = "SELECT id FROM purchase_inventory WHERE product_id ="
							+ productID + " AND id > 204026  ";

					rs_listOfPurchaseForProd = tcon.createStatement().executeQuery(sql_listOfPurchaseForProd);
					ProductDetails prodDetails = new ProductDetails(); // New Product Details Object

					int purchaseId = 0;
					while (rs_listOfPurchaseForProd.next()) {

						purchaseId = rs_listOfPurchaseForProd.getInt("id");

						StringBuffer qString = new StringBuffer(
								"SELECT pi.growth_condition, pi.quantity, (pi.quantity*pi.weight_in_grams) AS purchased, "
										+ "to_char(pi.date_added, 'MM/dd/YYYY') AS date_purchased," + "p.product_name, "
										+ "v.vendor_name, " + "c.category_name, " + "s.strain_name , "
										+ "s.id AS strainId," + "c.id AS categoryId" + "FROM purchase_inventory pi "
										+ "JOIN products p on pi.product_id = p.id "
										+ "JOIN vendors v on pi.vendor_id = v.id "
										+ "JOIN categories c on p.category_id = c.id "
										+ "JOIN strains s on p.strain_id = s.id " + "WHERE pi.id = ");

						qString.append(Integer.toString(purchaseId));

						rs = tcon.createStatement().executeQuery(qString.toString());

						if (rs.next()) {
							// category_id = rs.getInt(9);
							category_id = rs.getInt(10);
							totpurchase_wt = Double.parseDouble(rs.getString("purchased")) + totpurchase_wt;
							strain_name = rs.getString("strain_name");
							strainid = rs.getInt("id");

							boolean flowers = false;
							if (rs.getString("category_name").equalsIgnoreCase("Flower"))
								flowers = true;

							double inpackets = 0d;

							int purchaseCount = rs.getInt("quantity"), packetCount = 0, soldCount = 0;

							totpurchase_qty = purchaseCount + totpurchase_qty;

							rs1 = tcon.createStatement().executeQuery("SELECT COUNT(id), SUM(weight_in_grams) "
									+ "FROM packet_inventory " + "WHERE purchase_id = " + purchaseId);
							if (rs1.next()) {
								packetCount = rs1.getInt(1);
								inpackets = rs1.getDouble(2);

								totpacked_wt = inpackets + totpacked_wt;
								totpackets_qty = totpackets_qty + packetCount;
							}
							rs1.close();

							rs1 = tcon.createStatement()
									.executeQuery("SELECT COUNT(id), SUM(weight_in_grams) "
											+ "FROM packet_inventory WHERE sales_id > 0 " + "AND returns_detail_id = 0 "
											+ "AND purchase_id = " + purchaseId);

							if (rs1.next()) {
								soldCount = rs1.getInt(1);
								totsold_wt = totsold_wt + rs1.getDouble(2);
								totsold_qty = totsold_qty + soldCount;
							}
							rs1.close();

							/* Calculating total Adjusted and Returned Products */

							String retrndAndAdjustd = " WITH  " + "returned AS " + "(SELECT COUNT(*) AS tot_returned "
									+ " FROM packet_inventory " + " WHERE returns_detail_id > 0 AND purchase_id IN "
									+ "(" + purchaseId + ")" + "), " + "adjusted AS "
									+ "(SELECT SUM(quantity) AS tot_adjusted " + " FROM purchase_inventory "
									+ " WHERE growth_condition = 'Adjustment' " + " AND id IN" + "(" + purchaseId + ")"
									+ ")"

									+ "SELECT returned.*, adjusted.* FROM returned, adjusted ";

							rs_returndAndAdjsutd = tcon.createStatement().executeQuery(retrndAndAdjustd);

							while (rs_returndAndAdjsutd.next()) {

								tot_returnd = rs_returndAndAdjsutd.getInt("tot_returned") + tot_returnd;
								tot_adjusted = rs_returndAndAdjsutd.getInt("tot_adjusted") + tot_adjusted;
							}

							rs_returndAndAdjsutd.close();
							rs_returndAndAdjsutd = null;
							/***********/

							// remainingCount = purchaseCount - soldCount;

						}

					} // end of second while loop
					prodDetails.setStrain_name(strain_name);
					prodDetails.setStrainid(strainid);
					prodDetails.setTotal_purchase_qty(totpurchase_qty);
					prodDetails.setTotal_packet_qty(totpackets_qty);
					prodDetails.setTotal_sold_qty(totsold_qty);
					prodDetails.setAdjustment(tot_adjusted);
					prodDetails.setReturned(tot_returnd);

					double totalremaining_qty = totpackets_qty + tot_adjusted - totsold_qty - tot_returnd; // Calculating
																											// total
																											// remaining
																											// qty
					prodDetails.setTotal_remain_qty(totalremaining_qty);
					totalremaining_qty = 0.0;

					prodDetails.setTotal_purchase_weight(totpurchase_wt);
					prodDetails.setTotal_packed_weight(totpacked_wt);
					prodDetails.setTotal_sold_weight(totsold_wt);
					prodDetails.setTotal_remain_weight();
					prodDetails.setCategory_id(category_id);
					prodDetails.setProduct_id(productID);

					productDetailsList.add(prodDetails);

				

					strainid = 0;
					category_id = 0;
					totpurchase_qty = 0d;
					totpackets_qty = 0d;
					totsold_qty = 0d;
					totpurchase_wt = 0d;
					totpacked_wt = 0d;
					totsold_wt = 0d;
					tot_adjusted = 0;
					tot_returnd = 0;
					strain_name = "";

				}
			}

			rs.close();
			rs = null;

			rs_disnctPurchaseIdsForSalesid.close();
			rs_disnctPurchaseIdsForSalesid = null;

			rs_productForPurchaseId.close();
			rs_productForPurchaseId = null;

			rs_listOfPurchaseForProd.close();
			rs_listOfPurchaseForProd = null;

			rs1.close();
			rs1 = null;

			pst.close();
			pst = null;

			tcon.close();
			tcon = null;
		} catch (Exception e) {
			log.error(Exceptions.giveStackTrace(e));

		}

		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (rs_productForPurchaseId != null)
					rs_productForPurchaseId.close();
			} catch (Exception e) {
			}
			try {
				if (rs_listOfPurchaseForProd != null)
					rs_listOfPurchaseForProd.close();
			} catch (Exception e) {
			}
			try {
				if (rs_disnctPurchaseIdsForSalesid != null)
					rs_disnctPurchaseIdsForSalesid.close();
			} catch (Exception e) {
			}
			try {
				if (rs1 != null)
					rs1.close();
			} catch (Exception e) {
			}
			try {
				if (pst != null)
					pst.close();
			} catch (Exception e) {
			}
			try {
				if (tcon != null)
					tcon.close();
			} catch (Exception e) {
			}

		}

		for (int i = 0; i < listOfDistnctProdIds.size(); i++) {
			String resp = new UpdateMongoProductsStatus().updateMongoProductStockStatus(productID);
			logger.debug(
					"RESPONSE FROM MAIN APPLICATION AFTER SENDING MONGO ID FROM updateProductsAvailable-->" + resp);
		}
		return true;
	}


	
	
public void updateProducts(Integer productID, Integer shopId) {
	
}



public void updateProducts(Integer productID, Connection tcon) {
		
		ResultSet rs_listOfPurchaseForProd = null, 
				  rs                       = null, 
			 	  rs1                      = null,
                  rs_returndAndAdjsutd      =null;

		PreparedStatement pst = null;

		int strainid = 0;
		int category_id = 0;
		int tot_returnd=0;
		int tot_adjusted=0;
		double totpurchase_qty = 0d;
		double totpackets_qty = 0d;
		double totsold_qty = 0d;
		double totpurchase_wt = 0d;
		double totpacked_wt = 0d;
		double totsold_wt = 0d;
		String strain_name = "";

	try {
			String sql_listOfPurchaseForProd = "SELECT id FROM purchase_inventory WHERE product_id =" + productID+
					                           " AND id > 204026";

			rs_listOfPurchaseForProd = tcon.createStatement().executeQuery(sql_listOfPurchaseForProd);
			ProductDetails prodDetails = new ProductDetails(); // New Product Details Object

			int purchaseId = 0;
			while (rs_listOfPurchaseForProd.next()) {

				purchaseId = rs_listOfPurchaseForProd.getInt("id");

				StringBuffer qString = new StringBuffer(
						"SELECT pi.growth_condition, pi.quantity, (pi.quantity*pi.weight_in_grams) AS purchased, "
								+ "to_char(pi.date_added, 'MM/dd/YYYY') AS date_purchased," + "p.product_name, "
								+ "v.vendor_name, " + "c.category_name, " + "s.strain_name, " + "s.id," + "c.id "
								+ "FROM purchase_inventory pi " + "JOIN products p on pi.product_id = p.id "
								+ "JOIN vendors v on pi.vendor_id = v.id "
								+ "JOIN categories c on p.category_id = c.id " + "JOIN strains s on p.strain_id = s.id "
								+ "WHERE pi.id = ");

				qString.append(Integer.toString(purchaseId));

				rs = tcon.createStatement().executeQuery(qString.toString());

				if (rs.next()) {
				    category_id = rs.getInt(10);
					totpurchase_wt = Double.parseDouble(rs.getString("purchased")) + totpurchase_wt;
					strain_name = rs.getString("strain_name");
					strainid = rs.getInt("id");
                    double inpackets = 0d;
                    int purchaseCount = rs.getInt("quantity"), packetCount = 0, soldCount = 0;
                    
                    totpurchase_qty = purchaseCount + totpurchase_qty;
                   
                    rs1 = tcon.createStatement().executeQuery("SELECT COUNT(id), SUM(weight_in_grams) "
							+ "FROM packet_inventory " + "WHERE purchase_id = " + purchaseId);
					    if (rs1.next()) {
						    packetCount = rs1.getInt(1);
                            inpackets = rs1.getDouble(2);
						    totpacked_wt = inpackets + totpacked_wt;
                            totpackets_qty = totpackets_qty + packetCount;
                            }
					rs1.close();

					rs1 = tcon.createStatement().executeQuery("SELECT COUNT(id), SUM(weight_in_grams) "
							+ "FROM packet_inventory " + "WHERE sales_id > 0"
							 +"AND returns_detail_id = 0 "
							+ " AND purchase_id = " + purchaseId);

					if (rs1.next()) {
					    
						soldCount = rs1.getInt(1);
                        totsold_wt = totsold_wt + rs1.getDouble(2);
						totsold_qty = totsold_qty + soldCount;
					}
					rs1.close();
					}

				
				String retrndAndAdjustd = " WITH  " + "returned AS " + "(SELECT COUNT(*) AS tot_returned "
						+ " FROM packet_inventory " + " WHERE returns_detail_id > 0 AND purchase_id IN " + "("
						+ purchaseId + ")" + "), " +

						"adjusted AS " + "(SELECT SUM(quantity) AS tot_adjusted " + " FROM purchase_inventory "
						+ " WHERE growth_condition = 'Adjustment' " + " AND id IN" + "(" + purchaseId + ")"
						+ ")"

						+ "SELECT returned.*, adjusted.* FROM returned, adjusted ";

				

				rs_returndAndAdjsutd = tcon.createStatement().executeQuery(retrndAndAdjustd);
				while (rs_returndAndAdjsutd.next()) {

					tot_returnd = rs_returndAndAdjsutd.getInt("tot_returned") + tot_returnd;
					tot_adjusted = rs_returndAndAdjsutd.getInt("tot_adjusted") + tot_adjusted;
				}

				rs_returndAndAdjsutd.close();
				rs_returndAndAdjsutd = null;
				
				
				
				
			}
			prodDetails.setStrain_name(strain_name);
			prodDetails.setStrainid(strainid);
			prodDetails.setTotal_purchase_qty(totpurchase_qty);
			prodDetails.setTotal_packet_qty(totpackets_qty);
			prodDetails.setTotal_sold_qty(totsold_qty);
			prodDetails.setAdjustment(tot_adjusted);
			prodDetails.setReturned(tot_returnd);
			
			double totalremaining_qty =	totpackets_qty+tot_adjusted-totsold_qty-tot_returnd; //Calculating total remaining qty
            prodDetails.setTotal_remain_qty(totalremaining_qty);
			
			
			prodDetails.setTotal_purchase_weight(totpurchase_wt);
			prodDetails.setTotal_packed_weight(totpacked_wt);
			prodDetails.setTotal_sold_weight(totsold_wt);
			prodDetails.setTotal_remain_weight();
			prodDetails.setCategory_id(category_id);
			prodDetails.setProduct_id(productID);

			
			
			
			pst = tcon.prepareStatement(
					"UPDATE products_available SET category_id=?,strain_id=?,total_purchase_qty=?,total_packet_qty=?,total_sold_qty=?,total_remaining_qty=?,total_purchase_weight=?,\r\n"
							+ "total_packet_weight=?,total_sold_weight=?,total_remaining_weight=?,returned=?,adjusted=? WHERE product_id=?");

			pst.setInt(1, prodDetails.getCategory_id());
			pst.setInt(2, prodDetails.getStrainid());
			pst.setDouble(3, prodDetails.getTotal_purchase_qty());
			pst.setDouble(4, prodDetails.getTotal_packet_qty());
			pst.setDouble(5, prodDetails.getTotal_sold_qty());
			pst.setDouble(6, prodDetails.getTotal_remain_qty());
			pst.setDouble(7, prodDetails.getTotal_purchase_weight());
			pst.setDouble(8, prodDetails.getTotal_packed_weight());
			pst.setDouble(9, prodDetails.getTotal_sold_weight());
			pst.setDouble(10, prodDetails.getTotal_remain_weight());
			pst.setDouble(11, prodDetails.getReturned());
			pst.setDouble(12, prodDetails.getAdjustment());
			pst.setDouble(13, prodDetails.getProduct_id());
			
			if (pst.executeUpdate() == 0) {

				logger.error("was not able to update product_available table sucessfully - " + pst);

			}

	
		} catch (Exception e) {
			logger.error(Exceptions.giveStackTrace(e));
		}

		finally {
			try {if (rs != null)rs.close();} catch (Exception e) {}
			try {if (rs1 != null)rs1.close();} catch (Exception e) {}
			try {if (rs_listOfPurchaseForProd != null)rs_listOfPurchaseForProd.close();} catch (Exception e) {}
			try {if(rs_returndAndAdjsutd!=null) {rs_returndAndAdjsutd.close();}}catch(Exception e) {}
		}
	
		String resp=new UpdateMongoProductsStatus().updateMongoProductStockStatus(productID);
        logger.debug("RESPONSE FROM MAIN APPLICATION AFTER SENDING MONGO ID FROM updateProducts()-->"+resp);
	
	}








public static void main(String[] args) {
		new MasterInventoryService().updateProductsAvailable(532,0);

}












}
