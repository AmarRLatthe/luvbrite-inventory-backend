package com.luvbrite.jdbcutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.externalservice.FindDistance;

public class FindDistanceDTOMapper implements RowMapper<FindDistance>{

	@Override
	public FindDistance mapRow(ResultSet rs, int rowNum) throws SQLException {
		return	new FindDistance(rs.getString("address"));

	}

}
