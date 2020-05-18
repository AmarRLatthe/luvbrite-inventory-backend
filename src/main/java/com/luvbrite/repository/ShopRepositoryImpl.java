package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.luvbrite.model.CreateShopDTO;
import com.luvbrite.model.ShopDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ShopRepositoryImpl implements IShopRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	private BCryptPasswordEncoder bCryptPasswordEncoder =new BCryptPasswordEncoder();

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

	@Override
	public List<ShopDTO> getAllShops() {
		try {
			StringBuilder ShopsListQry = new StringBuilder();
				 
			ShopsListQry.append(" SELECT ")
							.append(" SHOPS.id, ")
							.append(" SHOPS.shop_name, ")
							.append(" SHOPS.domain, ")
							.append("  SHOP_OWNER.name AS shopOwner, ")
							.append(" SHOP_OWNER.email, ")
							.append(" SHOP_OWNER.username, ")
							.append(" USER_CREATOR.name AS shopCreator")
							.append(" FROM Shops ")
							.append("	JOIN USER_DETAILS AS SHOP_OWNER ON SHOPS.shop_admin_id = SHOP_OWNER.id  ")
							.append(" JOIN USER_DETAILS AS USER_CREATOR ON SHOPS.created_by = USER_CREATOR.id  ")
							.append(" WHERE SHOPS.is_active = true ")
							.append(" ORDER BY   ")
							.append(" SHOP_OWNER.id ")
							.append(" DESC ");
				return jdbcTemplate.query(ShopsListQry.toString(), new RowMapper<ShopDTO>() {

					@Override
					public ShopDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
						ShopDTO dto = new ShopDTO();
						dto.setShopId(rs.getInt("id"));
						dto.setShopOwnerName(rs.getString("shopOwner"));
						dto.setShopName(rs.getString("shop_name"));
						dto.setShopOwnerUsername(rs.getString("username"));
						dto.setDomain(rs.getString("domain"));
						dto.setCreatedBy(rs.getString("shopCreator"));
						dto.setEmail(rs.getString("email"));
						return dto;
					}
				});
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return Collections.emptyList();
		}
	}

	@Override
	public int countShopsByDomain(String domain) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SHOPS WHERE LOWER(domain) = LOWER(?)",new Object[] {domain}, Integer.class);
		} catch (Exception e) {
			log.error("message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int countShopsByShopNameNId(Integer id, String shopName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SHOPS WHERE LOWER(shop_name) = LOWER(?) AND id!= ? ",new Object[] {shopName,id}, Integer.class);
		} catch (Exception e) {
			log.error("message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int countShopsByDomainNId(Integer id, String domain) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SHOPS WHERE LOWER(domain) = LOWER(?) AND id!= ? ",new Object[] {domain,id}, Integer.class);
		} catch (Exception e) {
			log.error("message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

	@Transactional
	@Override
	public int updateShopById(Integer id, ShopDTO shop) {
		try {
			StringBuilder updateShopQry = new StringBuilder();
			updateShopQry.append(" UPDATE shops ")
								.append(" SET ")
								.append(" shop_name= ?,  ")
								.append(" domain=? ")
								.append(" WHERE id=? ");
			
			StringBuilder updateShopOwnerDetailsQry = new StringBuilder();
			updateShopOwnerDetailsQry.append("UPDATE user_details ")
									.append(" SET ")
									.append(" email=?, ")
									.append(" username=?,  ")
									.append(" name=? ")
									.append(" WHERE id = (")
									.append(" SELECT ")
									.append(" SHOPS.shop_admin_id ")
									.append(" FROM SHOPS  ")
									.append(" WHERE ")
									.append(" SHOPS.id = ?)");
			
				int updateShop = jdbcTemplate.update(updateShopQry.toString(),shop.getShopName(), shop.getDomain(),id);
			if(updateShop>0) {
				updateShop = jdbcTemplate.update(updateShopOwnerDetailsQry.toString(),shop.getEmail(),shop.getShopOwnerUsername(),shop.getShopOwnerName(),id);
			}
			return updateShop;
		} catch (Exception e) {
			log.error("message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}		
	}

	@Transactional
	@Override
	public int deleteShopById(Integer id) {
		try {
			int del = jdbcTemplate.update("UPDATE shops	SET is_active= false WHERE id=?",id);
			if(del>0) {
				del = jdbcTemplate.update(" UPDATE user_details	SET is_active= false  WHERE shop_id =?; ",id);
				if(del>0) 
					return del;
			}
			return -1;
		} catch (Exception e) {
			log.error("message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int updatePwdByshopId(Integer id, String password) {
		try {
			StringBuilder newPwd = new StringBuilder();
			newPwd.append("{bcrypt}")
					.append(bCryptPasswordEncoder.encode(password));
			return jdbcTemplate.update("UPDATE user_details	SET password=? 	WHERE id=(SELECT shop_admin_id FROM SHOPS WHERE id=?)", newPwd.toString(), id);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return -1;
		}
	}

}
