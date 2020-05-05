package com.luvbrite.repository;

import java.sql.ResultSet; 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.luvbrite.model.UserDetails;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserRepositoryImpl implements IUserRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public UserDetails findByUsername(String username) {
		
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT ")
					.append(" USER_DETAILS.*, ")
					.append("SHOPS.shop_name, ")
					.append("USER_TYPE_DETAILS.user_type, ")
					.append(" USER_TYPE_DETAILS.user_role_name ")	
					.append("FROM  USER_DETAILS ")
					.append("LEFT JOIN SHOPS ON USER_DETAILS.shop_id = SHOPS.id ")
					.append(" LEFT JOIN USER_TYPE_DETAILS ON USER_TYPE_DETAILS.id = USER_DETAILS.user_type_id WHERE USER_DETAILS.username = ")
					.append("'")
					.append(username)
					.append("'");
			UserDetails userDetails= jdbcTemplate.queryForObject(builder.toString(),new RowMapper<UserDetails>() {

				@Override
				public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					UserDetails userDetails = new UserDetails();
					
					userDetails.setId(rs.getInt("id"));
					userDetails.setOwnerId(rs.getInt("owner_id"));
					userDetails.setShopId(rs.getInt("shop_id"));
					userDetails.setUserTypeId(rs.getInt("user_type_id"));
					
					userDetails.setUsername(rs.getString("username"));
					userDetails.setPassword(rs.getString("password"));
					userDetails.setEmail(rs.getString("email"));
					userDetails.setShopName(rs.getString("shop_name"));
					userDetails.setUserType(rs.getString("user_type"));
					userDetails.setIsActive(rs.getBoolean("is_active"));
					String role = rs.getString("user_role_name");
					List<String> roles = new ArrayList<>();
					roles.add(role);
					userDetails.setUserRoles(roles);
					
					return userDetails;
				}
			});
			StringBuilder custRoleQuery = new StringBuilder();
			if(userDetails.getId()!=null) {
				custRoleQuery.append(" SELECT ")
				.append(" func_role_name ")
				.append(" FROM custom_roles_details ")
				.append(" JOIN user_custom_roles ON custom_roles_details.id = user_custom_roles.custom_role_id ")
				.append(" JOIN user_details ON user_details.id = user_custom_roles.user_id ")
				.append(" WHERE ")
				.append(" user_details.id = ")
				.append(userDetails.getId());
				List<String> customRoles = jdbcTemplate.queryForList(custRoleQuery.toString(), String.class);
				
				if( customRoles!=null && !customRoles.isEmpty() ) {
					userDetails.getUserRoles().addAll(customRoles);
				}
			}
			
			return userDetails;
		}catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(), e);
			return null;
		}
		
	}
	
	

}
