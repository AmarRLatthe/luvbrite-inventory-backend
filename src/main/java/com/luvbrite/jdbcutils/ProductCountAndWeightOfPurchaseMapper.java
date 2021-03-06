package com.luvbrite.jdbcutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.model.ProductCountAndWeightOfPurchase;

public class ProductCountAndWeightOfPurchaseMapper implements RowMapper<ProductCountAndWeightOfPurchase> {

	
	@Override
	public ProductCountAndWeightOfPurchase mapRow(ResultSet rs, int rowNum) throws SQLException {
	
		ProductCountAndWeightOfPurchase productCountAndPurchase =  new ProductCountAndWeightOfPurchase();
		
		productCountAndPurchase.setCount(rs.getInt(1));
		productCountAndPurchase.setTotalWeight(rs.getDouble(2));
		return productCountAndPurchase;
		
	}

	
}
