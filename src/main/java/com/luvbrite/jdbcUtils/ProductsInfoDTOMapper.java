package com.luvbrite.jdbcutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.model.ProductsExt;

public class ProductsInfoDTOMapper implements RowMapper<ProductsExt> {

	@Override
	public ProductsExt mapRow(ResultSet rs, int rowNum) throws SQLException {

		ProductsExt p = new ProductsExt();
		p.setId(rs.getInt("id"));
		p.setProductName(rs.getString("product_name"));
		p.setNickName(rs.getString("nick_name"));
		p.setCategoryId(rs.getInt("category_id"));
		p.setStrainId(rs.getInt("strain_id"));
		p.setStatus(rs.getString("status"));

		p.setCategoryName(rs.getString("category_name"));
		p.setStrainName(rs.getString("strain_name"));
		p.setFormattedDateAdded(rs.getString("date"));
		p.setMongo_productid(rs.getLong("mongo_productid"));

		return p;
	}

}
