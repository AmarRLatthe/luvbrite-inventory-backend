package com.luvbrite.jdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.model.AdjustedAndReturnedDTO;

public class ReturnedAndAdjustProductMapper implements RowMapper<AdjustedAndReturnedDTO> {

	@Override
	public AdjustedAndReturnedDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		AdjustedAndReturnedDTO adjustedAndreturnedProducts =  new AdjustedAndReturnedDTO();
		adjustedAndreturnedProducts.setAdjustedProduct(rs.getInt("tot_adjusted"));
		adjustedAndreturnedProducts.setReturnedProducts(rs.getInt("tot_returned"));
		return adjustedAndreturnedProducts;
	}

}
