package com.luvbrite.jdbcutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.model.ProductDetailsDTO;

public class ProductDetailsDTOMapper implements RowMapper<ProductDetailsDTO> {

	@Override
	public ProductDetailsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
	
		ProductDetailsDTO productDetailsDTO =  new ProductDetailsDTO();
		productDetailsDTO.setCategory_id(rs.getInt("categoryId"));
		productDetailsDTO.setTotal_purchase_weight(rs.getDouble("purchased"));
		productDetailsDTO.setStrain_name(rs.getString("strain_name"));
		productDetailsDTO.setStrainid(rs.getInt("strainid"));
		productDetailsDTO.setCategoryName(rs.getString("category_name"));
		productDetailsDTO.setTotal_purchase_qty(rs.getDouble("quantity"));
		return productDetailsDTO;
	}

}
