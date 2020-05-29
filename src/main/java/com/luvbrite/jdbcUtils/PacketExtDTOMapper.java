package com.luvbrite.jdbcutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.model.PacketExtDTO;

public class PacketExtDTOMapper implements RowMapper<PacketExtDTO> {

	@Override
	public PacketExtDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		PacketExtDTO packetExtDTO = new PacketExtDTO();

		packetExtDTO.setId(rs.getInt(1));
		packetExtDTO.setPurchaseId(rs.getInt("purchase_id"));

		String pc = rs.getString("packet_code");
		if ((pc.indexOf("MISC") == 0) && (pc.indexOf(">>") > 0)) {// If its an adjustment, extract the note part.
			packetExtDTO.setPacketCode(pc.split(">>")[0]);
			packetExtDTO.setProductName(pc.split(">>")[1]);
		} else {
			packetExtDTO.setPacketCode(pc);
			packetExtDTO.setProductName(rs.getString("product_name"));
		}

		packetExtDTO.setWeightInGrams(rs.getDouble("weight_in_grams"));
		packetExtDTO.setMarkedPrice(rs.getDouble("marked_price"));
		packetExtDTO.setShopId(rs.getInt("shop_id"));
		packetExtDTO.setSellingPrice(rs.getDouble("selling_price"));
		packetExtDTO.setSalesId(rs.getInt("sales_id"));
		packetExtDTO.setReturnDetailsId(rs.getInt("returns_detail_id"));

		packetExtDTO.setDateAdded(rs.getString("add_date"));
		packetExtDTO.setDateSold(rs.getString("sold_date"));

		packetExtDTO.setProductId(rs.getInt("product_id"));
		packetExtDTO.setShopName(rs.getString("shop_name"));
		packetExtDTO.setReturnReason(rs.getString("return_reason"));
		return packetExtDTO;
	}

}
