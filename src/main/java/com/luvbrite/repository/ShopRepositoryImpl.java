package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.luvbrite.model.CreateShopDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ShopRepositoryImpl implements IShopRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	@Override
	public int saveShop(CreateShopDTO shopDTO) {
		try {
			String shopQuery = "INSERT INTO SHOPS (shop_name,created_by,domain) VALUES (?,?,?) returning id";
			Integer shopid = jdbcTemplate.queryForObject(shopQuery,
					new Object[] { shopDTO.getShopName(), shopDTO.getCreatedBy() ,shopDTO.getDomain()}, Integer.class);

			String password = "{bcrypt}" + new BCryptPasswordEncoder().encode(shopDTO.getPassword());

			StringBuilder userAddQry = new StringBuilder();
			userAddQry.append("INSERT INTO user_details ")
					.append(" ( name,email, password, username, owner_id, shop_id, user_type_id,created_by ) ")
					.append("VALUES ( '")
					.append(shopDTO.getFullName())
					.append("' , '")
					.append(shopDTO.getEmail())
					.append("' , '")
					.append(password)
					.append("' , '")
					.append(shopDTO.getUserName())
					.append("' , ")
					.append(shopDTO.getOwnerId())
					.append(" , ")
					.append(shopid.toString())
					.append(" , ")
					.append(shopDTO.getUserTypeId())
					.append(" , ")
					.append(shopDTO.getCreatedBy()).append(" ) RETURNING id");

			Integer userId = jdbcTemplate.queryForObject(userAddQry.toString(), Integer.class);

			StringBuilder updateShop = new StringBuilder();
			updateShop.append("UPDATE SHOPS SET shop_admin_id = '").append(userId.toString()).append("' WHERE id = '")
					.append(shopid.toString()).append("'");

			int update = jdbcTemplate.update(updateShop.toString());

			log.info("updated data is {}", update);

			return update;
		} catch (Exception e) {
			log.error("message is {} and exception is {}", e.getMessage(), e);
			return -1;
		}

	}

	@Override
	public CreateShopDTO getByShopName(String shopName) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM SHOPS WHERE LOWER(shop_name) = LOWER(?)", new Object[] { shopName },
					new RowMapper<CreateShopDTO>() {

						@Override
						public CreateShopDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
							CreateShopDTO shopDTO = new CreateShopDTO();
							shopDTO.setShopName(rs.getString("shop_name"));
							shopDTO.setCreatedBy(rs.getInt("created_by"));
							shopDTO.setId(rs.getInt("id"));
							shopDTO.setIsActive(rs.getBoolean("is_active"));
							shopDTO.setShopAdminId(rs.getString("shop_admin_id"));
							return shopDTO;

						}
					});
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			
		}
		return null;
	}

	@Override
	public int countShopsByShopName(String shopName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SHOPS WHERE LOWER(shop_name) = LOWER(?)",new Object[] {shopName}, Integer.class);
		} catch (Exception e) {
			log.error("message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

}
