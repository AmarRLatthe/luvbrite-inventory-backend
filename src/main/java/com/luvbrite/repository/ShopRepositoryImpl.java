package com.luvbrite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
			String shopQuery = "INSERT INTO SHOPS (shop_name,created_by) VALUES (?,?) returning id";
			Integer shopid = jdbcTemplate.queryForObject(shopQuery, new Object[] { shopDTO.getShopName(),shopDTO.getCreatedBy() },
					Integer.class);

			String password = "{bcrypt}" + new BCryptPasswordEncoder().encode(shopDTO.getPassword());

			StringBuilder userAddQry = new StringBuilder();
			userAddQry.append("INSERT INTO user_details ")
					.append(" ( email, password, username, owner_id, shop_id, user_type_id,created_by ) ")
					.append("VALUES ( '")
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
					.append(shopDTO.getCreatedBy())
					.append(" ) RETURNING id");

			Integer userId = jdbcTemplate.queryForObject(userAddQry.toString(), Integer.class);

			StringBuilder updateShop = new StringBuilder();
			updateShop.append("UPDATE SHOPS SET shop_admin_id = '")
						.append(userId.toString())
					.append("' WHERE id = '")
						.append(shopid.toString())
					.append("'");

			int update = jdbcTemplate.update(updateShop.toString());

			log.info("updated data is {}", update);
			
			return update;
		} catch (Exception e) {
			log.error("message is {} and exception is {}", e.getMessage(), e);
			return -1;
		}

	}

}
