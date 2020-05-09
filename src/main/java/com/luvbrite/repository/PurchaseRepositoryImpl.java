package com.luvbrite.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.luvbrite.model.DriverDTO;
import com.luvbrite.model.PurchaseDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PurchaseRepositoryImpl implements IPurchaseRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public PurchaseDTO addPurchase(PurchaseDTO purchase) throws Exception{
		try {
			log.info("Came in purchase Repository ");
		

			String formatted_date="";
			
			
			StringBuilder _addPurchaseQry = new StringBuilder();
			                         
	     _addPurchaseQry.append("INSERT INTO purchase_inventory(product_id, growth_condition, quantity, weight_in_grams, ")
			            .append("unit_price, vendor_id, operator_comments, date_added, purchase_code,shop_id,) ")
			            .append("VALUES (?,?,?,?,?,?,?,to_date(?,'yyyy-MM-dd'),?,?)")
			            .append("RETURNING  TO_CHAR(date_added, 'MM/dd/yyyy') AS formatted_date;");
			
			
			 formatted_date = (String) jdbcTemplate.queryForObject(_addPurchaseQry.toString(),
																	new Object[] { purchase.getProductId(), 
																			       purchase.getGrowthCondition(), 
																			       purchase.getQuantity(),
																			       purchase.getWeightInGrams(), 
																			       purchase.getUnitPrice(), 
																			       purchase.getVendorId(),
																			       purchase.getOperatorComments(), 
																			       purchase.getDateAdded(), 
																			       purchase.getPurchaseCode(),
																			       purchase.getShopId()},
																	               String.class);
												
			purchase.setDateAdded(formatted_date);
			
			return purchase;

		} catch (Exception e) {
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			throw e;
			//return null;
		}
		
	}

	@Override
	public List<PurchaseDTO> getAllPurchases() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
