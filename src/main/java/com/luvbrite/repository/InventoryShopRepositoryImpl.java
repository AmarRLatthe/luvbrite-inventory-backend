package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.luvbrite.model.CategoryDTO;
import com.luvbrite.model.ShopInventoryDTO;
import com.luvbrite.utils.commons.ShopInvQryResource;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class InventoryShopRepositoryImpl implements IInventoryShopRepository {

	@Autowired
	private ShopInvQryResource shopInvQryResource;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<ShopInventoryDTO> getInventoryDetailsByShop(Integer shopId, String startDate) {
		String invQry = shopInvQryResource.getShopInvQry();
		startDate = "(date '" + startDate + "' + integer '1')";
		invQry = invQry.replaceAll("cmpdtformat", startDate);
		log.info("invQry {} ", invQry);
		try {
			return jdbcTemplate.query(invQry, new Object[] { shopId }, new RowMapper<ShopInventoryDTO>() {

				@Override
				public ShopInventoryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					ShopInventoryDTO dto = new ShopInventoryDTO();
					dto.setPacketCount(rs.getInt("packet_count"));
					dto.setRemainingCount(rs.getInt("remaining"));
					dto.setSoldCount(rs.getInt("sold_pkt"));
					dto.setProductId(rs.getInt("product_id"));
					dto.setProductName(
							rs.getString("product_name") + " (" + rs.getDouble("packet_weight_in_grams") + "g)");
					dto.setWeight(rs.getDouble("packet_weight_in_grams"));
					dto.setReturnedCount(rs.getInt("return_pkt"));
					dto.setAdjusted(rs.getInt("adjustment_count"));
					dto.setInventoryCost(rs.getDouble("total_packet_count_price") + rs.getDouble("adjustment_price")
							- rs.getDouble("sold_pkt_price"));
//					dto.setRemainingPackets(rs.getInt(""));
					return dto;
				}
			});
		} catch (Exception e) {
			log.error("Massege is {} and Exception is {}", e.getMessage(), e);
			return Collections.emptyList();
		}

	}

	@Override
	public List<CategoryDTO> getAllCategories() {
		log.info("Inside get all categories :::::::::: ");
		List<CategoryDTO> categories = new ArrayList<>();
		try {
			categories = jdbcTemplate.query(
					"SELECT *, TO_CHAR(date_added, 'MM/dd/yyyy HH:MI AM') as date FROM categories ORDER by id",
					new RowMapper<CategoryDTO>() {

						@Override
						public CategoryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
							CategoryDTO dto = new CategoryDTO();
							dto.setId(rs.getInt(1));
							dto.setCategoryName(rs.getString(2));
							dto.setFormattedDateAdded(rs.getString(4));
							return dto;
						}
					});
		} catch (Exception e) {
			log.error("Exception while getting category list : message is {}, exception is {} ", e.getMessage(), e);
		}
		return categories;
	}

}
