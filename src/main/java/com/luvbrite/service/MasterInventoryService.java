package com.luvbrite.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luvbrite.jdbcutils.ProductCountAndWeightOfPurchaseMapper;
import com.luvbrite.jdbcutils.ProductDetailsDTOMapper;
import com.luvbrite.jdbcutils.ReturnedAndAdjustProductMapper;
import com.luvbrite.model.AdjustedAndReturnedDTO;
import com.luvbrite.model.ProductCountAndWeightOfPurchase;
import com.luvbrite.model.ProductDetailsDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MasterInventoryService {

	@Autowired
	ProductsAvailableTableUpdateService updateProductAvailableService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean updateProductsAvailable(Integer salesID, Integer shopId) throws Exception {
		List<Integer> listOfDistinctProdIds = new ArrayList<Integer>();

		int tot_returnd = 0;
		int tot_adjusted = 0;
		double totpurchase_qty = 0d;
		double totpackets_qty = 0d;
		double totsold_qty = 0d;
		double totpacked_wt = 0d;
		double totsold_wt = 0d;
		
		ProductDetailsDTO childProductDetailsDTO = null;
		
		try {
			ArrayList<ProductDetailsDTO> productDetailsList = new ArrayList<ProductDetailsDTO>();

			StringBuilder sql_distinctPurchaseIdsForSalesId = new StringBuilder();
			sql_distinctPurchaseIdsForSalesId.append("SELECT DISTINCT purchase_id  FROM packet_inventory ")
					.append("WHERE sales_id = ?")
			.append(" AND shop_id = ?");

			List<Integer> distinctPurchaseIdsForSalesIds = null;
			distinctPurchaseIdsForSalesIds = jdbcTemplate.queryForList(sql_distinctPurchaseIdsForSalesId.toString(),
					new Object[] { salesID ,shopId}, Integer.class);

			log.info("Query  sql_distinctPurchaseIdsForSalesId :: "+sql_distinctPurchaseIdsForSalesId);
			log.info("Size of list distinctPurchaseIdsForSalesIds :: " + distinctPurchaseIdsForSalesIds.size());

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

					log.info("listOfPurchaseIdsForProducts :: " + listOfPurchaseIdsForProducts.size());

					for (Integer productPurchaseID : listOfPurchaseIdsForProducts) {

						StringBuilder qryBuffer = new StringBuilder();

						qryBuffer.append(
								"SELECT pi.growth_condition, pi.quantity, (pi.quantity*pi.weight_in_grams) AS purchased, ")
								.append("to_char(pi.date_added, 'MM/dd/YYYY') AS date_purchased,")
								.append("p.product_name, ")
								.append("v.vendor_name, ")
								.append("c.category_name, ")
								.append("s.strain_name, ").append("s.id AS strainId,").append("c.id AS categoryId ")
								.append("FROM purchase_inventory pi ")
								.append("JOIN products p on pi.product_id = p.id  ")
								.append("JOIN vendors v on pi.vendor_id = v.id  ")
								.append("JOIN categories c on p.category_id = c.id ")
								.append("JOIN strains s on p.strain_id = s.id ")
								.append("WHERE pi.id = ?");

						childProductDetailsDTO =  jdbcTemplate.queryForObject(qryBuffer.toString(),
								new Object[] { productPurchaseID }, new ProductDetailsDTOMapper());

						if (childProductDetailsDTO == null) {log.error("ProducDetails Object is null");  return false;}
						

						int soldCount = 0;

						totpurchase_qty = childProductDetailsDTO.getTotal_purchase_qty() + totpurchase_qty;

						totpurchase_qty = childProductDetailsDTO.getTotal_purchase_qty() + totpurchase_qty;

						StringBuilder strTotalCountAndWeightOfAProduct = new StringBuilder();
						strTotalCountAndWeightOfAProduct.append("SELECT COUNT(id), SUM(weight_in_grams) ")
								.append("FROM packet_inventory ").append("WHERE purchase_id = ").append("?");

						ProductCountAndWeightOfPurchase productCountAndPurchase = jdbcTemplate.queryForObject(
								strTotalCountAndWeightOfAProduct.toString(), new Object[] { productPurchaseID },
								new ProductCountAndWeightOfPurchaseMapper());

						totpacked_wt = productCountAndPurchase.getTotalWeight() + totpacked_wt;
						totpackets_qty = productCountAndPurchase.getCount() + totpackets_qty;

						StringBuilder strTotalCountAndWeightOfAReturnedProduct = new StringBuilder();
						strTotalCountAndWeightOfAReturnedProduct.append("SELECT COUNT(id), SUM(weight_in_grams) ")
								.append("FROM packet_inventory ").append("AND returns_detail_id = 0 ")
								.append("AND purchase_id =  ").append("?");

						ProductCountAndWeightOfPurchase soldProductCountAndWeight = jdbcTemplate.queryForObject(
								strTotalCountAndWeightOfAProduct.toString(), new Object[] { productPurchaseID },
								new ProductCountAndWeightOfPurchaseMapper());

						soldCount = soldProductCountAndWeight.getCount();
						totsold_wt = totsold_wt + soldProductCountAndWeight.getTotalWeight();
						totsold_qty = totsold_qty + soldCount;

						StringBuilder adjustedAndReturned = new StringBuilder();
						adjustedAndReturned.append(" WITH ")
						        .append("returned AS  ")
								.append("(SELECT COUNT(*) AS tot_returned ")
								.append(" FROM packet_inventory ")
								.append(" WHERE returns_detail_id > 0 AND purchase_id IN ")
								.append("(")
								.append("?")
								.append(")")
								.append("), ")
								.append("adjusted AS ")
								.append("(SELECT SUM(quantity) AS tot_adjusted ")
								.append(" FROM purchase_inventory ")
								.append(" WHERE growth_condition = 'Adjustment' ")
								.append(" AND id IN")
								.append("(?)")
								.append(")")
								.append("SELECT returned.*, adjusted.* FROM returned, adjusted ");

						List<AdjustedAndReturnedDTO> adjustedAndReturnedCount = jdbcTemplate
								.query(adjustedAndReturned.toString(),new Object[] { productPurchaseID ,productPurchaseID} ,new ReturnedAndAdjustProductMapper());

						for (AdjustedAndReturnedDTO adjustedNdReturnd : adjustedAndReturnedCount) {
							tot_returnd = adjustedNdReturnd.getReturnedProducts() + tot_returnd;
							tot_adjusted = adjustedNdReturnd.getAdjustedProduct() + tot_adjusted;
						}

						childProductDetailsDTO.setReturned(tot_returnd);
						childProductDetailsDTO.setReturned(tot_adjusted);
					

					}

					if (childProductDetailsDTO != null) {
						
						log.info("Child Product Details is not null");
						double totalremaining_qty = totpackets_qty + tot_adjusted - totsold_qty - tot_returnd;
						childProductDetailsDTO.setTotal_remain_qty(totalremaining_qty);

						updateProductAvailableService.updateProductsAvailable(childProductDetailsDTO, shopId);
					} else {
						log.info("Product Details is  null");
					}

					productDetailsList.add(childProductDetailsDTO);

					// Updating products available table

					totpurchase_qty = 0d;
					totpackets_qty = 0d;
					totsold_qty = 0d;
					totpacked_wt = 0d;
					totsold_wt = 0d;
					tot_adjusted = 0;
					tot_returnd = 0;
				}

			}

		} catch (Exception e) {
			log.error("Exception occured while maintaing master table ", e);
			throw e;
		}
		return false;
	}



	public void updateProducts(Integer productID, Integer shopId) throws Exception {
		
		int tot_returnd = 0;
		int tot_adjusted = 0;
		double totpurchase_qty = 0d;
		double totpackets_qty = 0d;
		double totsold_qty = 0d;
		double totpacked_wt = 0d;
		double totsold_wt = 0d;
		
		ProductDetailsDTO childProductDetailsDTO = null;
		ArrayList<ProductDetailsDTO> productDetailsList = new ArrayList<ProductDetailsDTO>();
		
		StringBuilder sql_listOfPurchaseIdsForProd  = new StringBuilder();
		sql_listOfPurchaseIdsForProd
		.append("SELECT")
		.append("id ")
		.append("FROM ")
		.append("purchase_inventory")
		.append("WHERE")
		.append("product_id = ")
		.append("?")
		.append("AND id > 204026")
		.append("AND shop_id = ")
		.append("?");
		
	

		log.info("Query sql_listOfPurchaseIdsForProd ::" + sql_listOfPurchaseIdsForProd);

		List<Integer> listOfPurchaseIdsForProducts = jdbcTemplate.queryForList(sql_listOfPurchaseIdsForProd.toString(),
				new Object[] {productID,shopId}, Integer.class);

		log.info("listOfPurchaseIdsForProducts :: " + listOfPurchaseIdsForProducts.size());

		for (Integer productPurchaseID : listOfPurchaseIdsForProducts) {

			StringBuilder qryBuffer = new StringBuilder();

			qryBuffer.append(
					"SELECT pi.growth_condition, pi.quantity, (pi.quantity*pi.weight_in_grams) AS purchased, ")
					.append("to_char(pi.date_added, 'MM/dd/YYYY') AS date_purchased,")
					.append("p.product_name, ")
					.append("v.vendor_name, ")
					.append("c.category_name, ")
					.append("s.strain_name, ").append("s.id AS strainId,").append("c.id AS categoryId ")
					.append("FROM purchase_inventory pi ")
					.append("JOIN products p on pi.product_id = p.id  ")
					.append("JOIN vendors v on pi.vendor_id = v.id  ")
					.append("JOIN categories c on p.category_id = c.id ")
					.append("JOIN strains s on p.strain_id = s.id ")
					.append("WHERE pi.id = ?");

			log.info("qryBuffer :: "+qryBuffer.toString());
			log.info("productPurchaseID :: "+productPurchaseID);
			childProductDetailsDTO = (ProductDetailsDTO) jdbcTemplate.queryForObject(qryBuffer.toString(),
					new Object[] { productPurchaseID }, new ProductDetailsDTOMapper());

			if (childProductDetailsDTO == null) {log.error("ProducDetails Object is null");  return ;}

			int soldCount = 0;

			totpurchase_qty = childProductDetailsDTO.getTotal_purchase_qty() + totpurchase_qty;

			totpurchase_qty = childProductDetailsDTO.getTotal_purchase_qty() + totpurchase_qty;

			StringBuilder strTotalCountAndWeightOfAProduct = new StringBuilder();
			strTotalCountAndWeightOfAProduct.append("SELECT COUNT(id), SUM(weight_in_grams) ")
					.append("FROM packet_inventory ").append("WHERE purchase_id = ").append("?");

			ProductCountAndWeightOfPurchase productCountAndPurchase = jdbcTemplate.queryForObject(
					strTotalCountAndWeightOfAProduct.toString(), new Object[] { productPurchaseID },
					new ProductCountAndWeightOfPurchaseMapper());

			totpacked_wt = productCountAndPurchase.getTotalWeight() + totpacked_wt;
			totpackets_qty = productCountAndPurchase.getCount() + totpackets_qty;

			StringBuilder strTotalCountAndWeightOfAReturnedProduct = new StringBuilder();
			strTotalCountAndWeightOfAReturnedProduct.append("SELECT COUNT(id), SUM(weight_in_grams) ")
					.append("FROM packet_inventory ").append("AND returns_detail_id = 0 ")
					.append("AND purchase_id =  ").append("?");

			ProductCountAndWeightOfPurchase soldProductCountAndWeight = jdbcTemplate.queryForObject(
					strTotalCountAndWeightOfAProduct.toString(), new Object[] { productPurchaseID },
					new ProductCountAndWeightOfPurchaseMapper());

			soldCount = soldProductCountAndWeight.getCount();
			totsold_wt = totsold_wt + soldProductCountAndWeight.getTotalWeight();
			totsold_qty = totsold_qty + soldCount;

			StringBuilder adjustedAndReturned = new StringBuilder();
			adjustedAndReturned.append(" WITH ").append("returned AS  ")
					.append("(SELECT COUNT(*) AS tot_returned ")
					.append(" FROM packet_inventory ")
					.append(" WHERE returns_detail_id > 0 AND purchase_id IN ")
					.append("(")
					.append("?")
					.append(")")
					.append("), ")
					.append("adjusted AS ")
					.append("(SELECT SUM(quantity) AS tot_adjusted ")
					.append(" FROM purchase_inventory ")
					.append(" WHERE growth_condition = 'Adjustment' ")
					.append(" AND id IN")
					.append("(?)")
					.append(")")
					.append("SELECT returned.*, adjusted.* FROM returned, adjusted ");

			List<AdjustedAndReturnedDTO> adjustedAndReturnedCount = jdbcTemplate
					.query(adjustedAndReturned.toString(),new Object[] { productPurchaseID ,productPurchaseID} ,new ReturnedAndAdjustProductMapper());

			for (AdjustedAndReturnedDTO adjustedNdReturnd : adjustedAndReturnedCount) {
				tot_returnd = adjustedNdReturnd.getReturnedProducts() + tot_returnd;
				tot_adjusted = adjustedNdReturnd.getAdjustedProduct() + tot_adjusted;
			}

			childProductDetailsDTO.setReturned(tot_returnd);
			childProductDetailsDTO.setReturned(tot_adjusted);

		}

		if (childProductDetailsDTO != null) {
			
			log.info("Child Product Details is not null");
			double totalremaining_qty = totpackets_qty + tot_adjusted - totsold_qty - tot_returnd;
			childProductDetailsDTO.setTotal_remain_qty(totalremaining_qty);

			// Updating products available table
			updateProductAvailableService.updateProductsAvailable(childProductDetailsDTO, shopId);
		} else {
			log.info("Product Details is  null");
		}

		productDetailsList.add(childProductDetailsDTO);

		

	
	}


	
	



	

}
