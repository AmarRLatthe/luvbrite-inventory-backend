package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luvbrite.model.ProductDetailsDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductsAvailableTableUpdateService implements IProductsAvailableTableUpdate {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public boolean updateProductsAvailable(ProductDetailsDTO productDetailsDTO,Integer shopId) throws Exception {

		StringBuffer productsAvailableUpdateQry = new StringBuffer();
		
		productsAvailableUpdateQry
		.append("UPDATE products_available SET ")
		.append("category_id=?,")
		.append("strain_id=?,")
		.append("total_purchase_qty=?,")
		.append("total_packet_qty=?,")
		.append("total_sold_qty=?,")
		.append("total_remaining_qty=?,")
		.append("total_purchase_weight=?, ")
		.append("total_packet_weight=?,")
		.append("total_sold_weight=?,")
		.append("total_remaining_weight=?,")
	    .append("returned=?,")
	    .append("adjusted=? ")
	    .append("WHERE product_id=?")
	    .append("AND")
	    .append("WHERE shop_id=?");

		int i = jdbcTemplate.update(productsAvailableUpdateQry.toString(),
				new Object[] {productDetailsDTO.getCategory_id(), 
						      productDetailsDTO.getStrainid(),
						      productDetailsDTO.getTotal_purchase_qty(), 
						      productDetailsDTO.getTotal_packet_qty(),
						      productDetailsDTO.getTotal_sold_qty(), 
						      productDetailsDTO.getTotal_remain_qty(),
					 	      productDetailsDTO.getReturned(), 
					 	      productDetailsDTO.getAdjustment(),
						      productDetailsDTO.getProduct_id(),
						      shopId});

		if (i == 0) {
			log.error("was not able to update product_available table sucessfully");
			return false;
		}
    
	
		log.info("Successfully updated product_available table");
	  return true;

	}

}
