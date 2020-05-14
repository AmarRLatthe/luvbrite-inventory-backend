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

import com.luvbrite.model.UserDetails;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OperatorRepositoryImpl implements IOperatorRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public OperatorRepositoryImpl() {
		bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}

	@Override
	public int saveOperator(UserDetails operator) {
		try {
			StringBuilder saveOperatorQry = new StringBuilder();
			saveOperatorQry.append(" INSERT INTO user_details ")
					.append(" ( name,email, password, username, owner_id, shop_id, user_type_id,  created_by) ")
					.append(" VALUES (?,?, ?, ?, ?, ?, (SELECT id FROM user_type_details WHERE user_type= ?), ?) ");
			return jdbcTemplate.update(saveOperatorQry.toString(), operator.getName(),operator.getEmail(),
					"{bcrypt}"+bCryptPasswordEncoder.encode(operator.getPassword()), operator.getUsername(), operator.getOwnerId(),
					operator.getShopId(), operator.getUserType(), operator.getCreatedBy());
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return -1;
		}
		
	}

	@Override
	public List<UserDetails> getOperatorsDataByShopId(Integer shopId) {
		try {
			StringBuilder operatorListQry = new StringBuilder();
			operatorListQry.append(" SELECT ")
							.append(" user_details.*, ")
							.append(" TO_CHAR(user_details.created_at, 'MM/dd/yyyy HH:MI AM') as date , ")
							.append(" user_type_details.user_type, ")
							.append(" ud.username as createdByUser ")
							.append(" FROM user_details ")
							.append(" JOIN user_type_details ON user_details.user_type_id= user_type_details.id ")
							.append(" LEFT JOIN user_details as ud ON user_details.created_by = ud.id ")
							.append(" WHERE ")
							.append(" user_details.shop_id = ? ");
			return jdbcTemplate.query(operatorListQry.toString(), new Object[] {shopId}, new RowMapper<UserDetails>() {

				@Override
				public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					UserDetails user = new UserDetails();
					
					user.setId(rs.getInt("id"));
					user.setName(rs.getString("name"));
					user.setOwnerId(rs.getInt("owner_id"));
					user.setShopId(rs.getInt("shop_id"));
					user.setUserTypeId(rs.getInt("user_type_id"));
					user.setCreatedAt(rs.getString("date"));
					user.setUsername(rs.getString("username"));
					user.setEmail(rs.getString("email"));
					user.setUserType(rs.getString("user_type"));
					user.setIsActive(rs.getBoolean("is_active"));
					user.setCreatedByName(rs.getString("createdByUser"));
					return user;
				}
			});			
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return Collections.emptyList();
		}
	}

	@Override
	public int updateOperatorById(int id, UserDetails operator) {
		try {
			StringBuilder updateOperator = new StringBuilder();
			updateOperator.append("UPDATE user_details ")
					.append(" SET ")
					.append(" email= ?, ")
					.append(" username= ?, ")
					.append("  user_type_id= ")
					.append(" ( SELECT id  ")
					.append(" FROM user_type_details WHERE user_type = ? ) ,")
					.append(" name=? ")
					.append(" WHERE ")
					.append(" id = ?");
			
			return jdbcTemplate.update(updateOperator.toString(),
										new Object[] {
												operator.getEmail(),
												operator.getUsername(),
												operator.getUserType(),
												operator.getName(),
												id
										});
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return -1;
		}	
	}

}
