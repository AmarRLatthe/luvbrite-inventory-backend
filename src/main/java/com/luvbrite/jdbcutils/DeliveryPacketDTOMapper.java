package com.luvbrite.jdbcutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.model.DeliveredPacketDTO;

public class DeliveryPacketDTOMapper  implements RowMapper<DeliveredPacketDTO>{

	@Override
	public DeliveredPacketDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

		DeliveredPacketDTO	deliveredPacket = new DeliveredPacketDTO();
		deliveredPacket.setItems(rs.getInt("items"));
		deliveredPacket.setProductName(rs.getString("product_name"));
		deliveredPacket.setTotal(rs.getFloat("selling_price"));

		return deliveredPacket;
	}

}
