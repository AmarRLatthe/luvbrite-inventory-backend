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

import com.luvbrite.model.AuthoritiesDTO;
import com.luvbrite.model.PermissionDTO;
import com.luvbrite.model.UserDetails;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OperatorRepositoryImpl implements IOperatorRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private IShopRepository iShopRepository;

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
			return jdbcTemplate.update(saveOperatorQry.toString(), operator.getName(), operator.getEmail(),
					"{bcrypt}" + bCryptPasswordEncoder.encode(operator.getPassword()), operator.getUsername(),
					operator.getOwnerId(), operator.getShopId(), operator.getUserType(), operator.getCreatedBy());
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}

	}

	@Override
	public List<UserDetails> getOperatorsDataByShopId(Integer shopId) {
		try {
			StringBuilder operatorListQry = new StringBuilder();
			operatorListQry.append(" SELECT ").append(" user_details.*, ")
					.append(" TO_CHAR(user_details.created_at, 'MM/dd/yyyy HH:MI AM') as date , ")
					.append(" user_type_details.user_type, ").append(" ud.username as createdByUser ")
					.append(" FROM user_details ")
					.append(" JOIN user_type_details ON user_details.user_type_id= user_type_details.id ")
					.append(" LEFT JOIN user_details as ud ON user_details.created_by = ud.id ").append(" WHERE ")
					.append(" user_details.shop_id = ? ").append(" AND").append(" user_details.is_active= true ")
					.append(" ORDER BY user_details.created_at DESC  ");
			return jdbcTemplate.query(operatorListQry.toString(), new Object[] { shopId },
					new RowMapper<UserDetails>() {

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
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public int updateOperatorById(int id, UserDetails operator) {
		try {
			StringBuilder updateOperator = new StringBuilder();
			updateOperator.append("UPDATE user_details ").append(" SET ").append(" email= ?, ").append(" username= ?, ")
					.append("  user_type_id= ").append(" ( SELECT id  ")
					.append(" FROM user_type_details WHERE user_type = ? ) ,").append(" name=? ").append(" WHERE ")
					.append(" id = ?");

			return jdbcTemplate.update(updateOperator.toString(), new Object[] { operator.getEmail(),
					operator.getUsername(), operator.getUserType(), operator.getName(), id });
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int deleteOperatorById(Integer id) {
		try {
			return jdbcTemplate.update(" UPDATE user_details	SET is_active= false WHERE id=?  ", id);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int updatePwdByOperatorId(Integer id, String password) {
		try {
			StringBuilder newPwd = new StringBuilder();
			newPwd.append("{bcrypt}").append(bCryptPasswordEncoder.encode(password));
			return jdbcTemplate.update("UPDATE user_details	SET password=? 	WHERE id=?", newPwd.toString(), id);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public List<String> getListOfAllPermissions() {
		try {
			return jdbcTemplate.query("SELECT funct_name FROM custom_roles_details", new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString(1);
				}

			});
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return Collections.emptyList();
		}

	}

	@Override
	public List<String> getListOfAllowedPermissionById(Integer id) {
		try {
			StringBuilder qry = new StringBuilder();
			qry.append(" SELECT DISTINCT(funct_name) ").append(" FROM USER_DETAILS ")
					.append(" RIGHT JOIN USER_CUSTOM_ROLES ON USER_DETAILS.id = USER_CUSTOM_ROLES.user_id ")
					.append(" RIGHT JOIN CUSTOM_ROLES_DETAILS ON USER_CUSTOM_ROLES.custom_role_id = CUSTOM_ROLES_DETAILS.id ")
					.append(" WHERE  ").append(" USER_DETAILS.id = ? ");

			return jdbcTemplate.query(qry.toString(), new Object[] { id }, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString(1);
				}
			});
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return Collections.emptyList();
		}

	}

	@Override
	public String getUserTypeById(Integer id) {
		try {

			StringBuilder qry = new StringBuilder();
			qry.append(" SELECT USER_TYPE_DETAILS.user_type ").append("  FROM USER_TYPE_DETAILS  ").append(" WHERE ")
					.append(" USER_TYPE_DETAILS.id =  ").append(" ( ").append(" SELECT user_details.user_type_id ")
					.append(" FROM user_details ").append(" WHERE ").append(" user_details.id= ? ").append(" ) ");

			return jdbcTemplate.queryForObject(qry.toString(), new Object[] { id }, String.class);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return "";
		}
	}

	@Transactional
	@Override
	public int authoritiesGrantByUserId(AuthoritiesDTO authorities) {
		try {
			List<Integer> authoritiesIds = iShopRepository.getIdsFromShopsNames(authorities.getAuthorities());
			 jdbcTemplate.update("DELETE FROM manager_shop_access_details WHERE manager_id=?" , authorities.getId());
			StringBuilder qry = new StringBuilder();
			qry.append(" INSERT ")
			.append(" INTO ")
			.append(" manager_shop_access_details ")
			.append(" ( ")
			.append(" manager_id ")
			.append(" , ")
			.append(" shop_id ")
			.append(" ) ") 
			.append(" VALUES ");
			for(int i = 0;i<authoritiesIds.size()-1;i++) {
				qry.append(" (");
				qry.append(authorities.getId());
				qry.append(" ,");
				qry.append(authoritiesIds.get(i));
				qry.append(" ) ,");
			}
		qry.append(" (");
		qry.append(authorities.getId());
		qry.append(" ,");
		qry.append(authoritiesIds.get(authoritiesIds.size()-1));
		qry.append(" )");
		return jdbcTemplate.update(qry.toString());
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
		
	}

	@Transactional
	@Override
	public int permissionGrantByUserId(PermissionDTO permission) {
		try {
			StringBuilder qry = new StringBuilder();
			int totalPlace = permission.getPermissions().length;
			qry.append(" SELECT ")
			.append(" id ")
			.append(" FROM ")
			.append(" custom_roles_details ")
			.append(" WHERE ")
			.append(" custom_roles_details.funct_name ")
			.append(" IN  ")
			.append("(");	
			if(totalPlace>1) {
				for(int i = 0 ;i<totalPlace-1;i++) {
					qry.append(" ? ,");
				}
			}
			qry.append(" ? ");
			qry.append(" ) ");	
			
			List<Integer> permissionIds = jdbcTemplate.query(qry.toString(),permission.getPermissions(), new RowMapper<Integer>() {
				@Override
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt(1);
				}
			});
			
			int upd= jdbcTemplate.update("DELETE FROM user_custom_roles WHERE user_id=?",permission.getId());
			log.info("i is {}",upd);
			StringBuilder qry1 = new StringBuilder();
			qry1.append(" INSERT ")
				.append(" INTO ")
				.append(" user_custom_roles ")
				.append(" ( ")
				.append(" user_id ")
				.append(" , ")
				.append(" custom_role_id ")
				.append(" ) ") 
				.append(" VALUES ");
				for(int i = 0;i<totalPlace-1;i++) {
					qry1.append(" (");
					qry1.append(permission.getId());
					qry1.append(" ,");
					qry1.append(permissionIds.get(i));
					qry1.append(" ),");
				}
			qry1.append(" (");
			qry1.append(permission.getId());
			qry1.append(" ,");
			qry1.append(permissionIds.get(totalPlace-1));
			qry1.append(" )");
					
			log.info("permission ids {}",permissionIds);
			return jdbcTemplate.update(qry1.toString());
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

}
