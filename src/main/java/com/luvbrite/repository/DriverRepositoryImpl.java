package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.luvbrite.model.DriverDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DriverRepositoryImpl implements IDriverRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public int saveDriver(DriverDTO driver) {
		try {
			log.info("Came in driver Repository ");
			StringBuilder driverAddQry = new StringBuilder();
			driverAddQry.append("INSERT INTO drivers ")
					.append(" (driver_name, user_name, phone_number, shop_id, created_by) ")
					.append(" VALUES ( ?, ?, ?, ?, ?) ");
			return jdbcTemplate.update(driverAddQry.toString(),driver.getDriverName(), driver.getUserName(), driver.getPhoneNumber(),driver.getShopId(),driver.getCreatedBy());
		}catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public List<DriverDTO> getDriversByShopId(Integer shopId) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT DRIVERS.*, ")	
			.append(" TO_CHAR(date_added, 'MM/dd/yyyy HH:MI AM') as date, ")
			.append("  USER_DETAILS.username, ")
			.append("  USER_DETAILS.id as user_id  ")
			.append(" FROM DRIVERS JOIN USER_DETAILS ON USER_DETAILS.id = DRIVERS.created_by ")
			.append("WHERE DRIVERS.SHOP_ID = ? ")
			.append(" AND  DRIVERS.IS_ACTIVE = true")
			.append(" ORDER by id DESC");
	
		
		try {
			return jdbcTemplate.query(builder.toString(),new Object[]{shopId}, new RowMapper<DriverDTO>() {

				@Override
				public DriverDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					DriverDTO dto = new DriverDTO();
					dto.setDriverName(rs.getString("driver_name"));
					dto.setUserName(rs.getString("user_name"));
					dto.setPhoneNumber(rs.getString("phone_number"));
					dto.setStatus(rs.getInt("status"));
					dto.setDateAdded(rs.getString("date"));
					dto.setId(rs.getInt("id"));
					dto.setCreatedBy(rs.getInt("created_by"));
					dto.setCreatedByName(rs.getString("username"));
					dto.setIsActive(rs.getBoolean("is_active"));
					return dto;
				}
			});
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return Collections.emptyList();
		}
	}

	@Override
	public DriverDTO findByDriverName(String driverName) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM drivers WHERE LOWER(driver_name) = LOWER(?)", new Object[] {driverName},new RowMapper<DriverDTO>() {

				@Override
				public DriverDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					DriverDTO dto = new DriverDTO();
					dto.setDriverName(rs.getString("driver_name"));
					dto.setPhoneNumber(rs.getString("phone_number"));
					dto.setStatus(rs.getInt("status"));
					dto.setDateAdded(rs.getString("date"));
					dto.setId(rs.getInt("id"));
					dto.setCreatedBy(rs.getInt("created_by"));
					
					return dto;
				}
				
			});
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
		}
		return null;
	}

	@Override
	public int countDriverByDriverName(String driverName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM drivers WHERE LOWER(driver_name) = LOWER(?)",new Object[] {driverName} ,Integer.class);			
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public int updateDriverById(int id, DriverDTO driver) {
			try {
				StringBuilder updateDriverQry = new StringBuilder();
				updateDriverQry.append(" UPDATE public.drivers ")
								.append(" SET  ")
								.append(" driver_name=?,  ")
								.append(" user_name=?,  ")
								.append(" phone_number=? ")
								.append(" WHERE  ")
								.append(" id=?; ");
				
				return jdbcTemplate.update(updateDriverQry.toString(),driver.getDriverName(),driver.getUserName(),driver.getPhoneNumber(),id);
			} catch (Exception e) {
				log.error("message is {} and Exception is {}", e.getMessage(), e);
				return -1;
			}
	}

	@Override
	public int countDriverByDriverNameNId(int id,String driverName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM drivers WHERE LOWER(driver_name) = LOWER(?) AND id != ?",new Object[] {driverName,id} ,Integer.class);			
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}


	@Override
	public int deleteDriverById(Integer id) {
		try {
			return jdbcTemplate.update(" UPDATE drivers SET is_active= false WHERE id=? ",id);
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}


}
