package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.luvbrite.model.VendorDTO;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@NoArgsConstructor
public class VendorRepositoryImpl implements IVendorRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int saveVendor(VendorDTO vendor) {
		try {
			StringBuilder creatVendorQry = new StringBuilder();
			creatVendorQry.append(" INSERT INTO vendors ")
					.append(" (vendor_name, company, address, city, state, zipcode, phone, email, website, shop_id, created_by) ")
					.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
			return jdbcTemplate.update(creatVendorQry.toString(), vendor.getVendorName(), vendor.getCompany(),
					vendor.getAddress(), vendor.getCity(), vendor.getState(), vendor.getZipcode(), vendor.getPhone(),
					vendor.getEmail(), vendor.getWebsite(), vendor.getShopId(), vendor.getCreatedBy());
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}

	}

	@Override
	public List<VendorDTO> getVendorsDataByShopId(Integer shopId) {
		try {
			
			StringBuilder builder = new StringBuilder();
				builder.append("SELECT VENDORS.*, ")	
					.append(" TO_CHAR(date_added, 'MM/dd/yyyy HH:MI AM') as date, ")
					.append("  USER_DETAILS.username, ")
					.append("  USER_DETAILS.id as user_id  ")
					.append(" FROM VENDORS JOIN USER_DETAILS ON USER_DETAILS.id = VENDORS.created_by ")
					.append("WHERE VENDORS.SHOP_ID = ? ")
					.append(" AND ")
					.append(" VENDORS.is_active = true ")
					.append(" ORDER by id DESC");
			
			return jdbcTemplate.query(builder.toString(), new Object[] { shopId }, new RowMapper<VendorDTO>() {

						@Override
						public VendorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
							VendorDTO dto = new VendorDTO();
							dto.setVendorId(rs.getInt("id"));
							dto.setVendorName(rs.getString("vendor_name"));
							dto.setCompany(rs.getString("company"));
							dto.setEmail(rs.getString("email"));
							dto.setPhone(rs.getString("phone"));
							dto.setAddress(rs.getString("address"));
							dto.setCity(rs.getString("city"));
							dto.setState(rs.getString("state"));
							dto.setZipcode(rs.getString("zipcode"));
							dto.setWebsite(rs.getString("website"));
							dto.setDateAdded(rs.getString("date"));
							dto.setCreatedBy(rs.getInt("created_by"));
							dto.setCreatedByName(rs.getString("username"));
							return dto;
						}
					});
		} catch (Exception e) {
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public VendorDTO findByVendorName(String vendorName) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM VENDORS WHERE LOWER(vendor_name) = LOWER(?)", new Object[] {vendorName},new RowMapper<VendorDTO>() {
				@Override
				public VendorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					VendorDTO dto = new VendorDTO();
					dto.setVendorId(rs.getInt("id"));
					dto.setVendorName(rs.getString("vendor_name"));
					dto.setCompany(rs.getString("company"));
					dto.setEmail(rs.getString("email"));
					dto.setPhone(rs.getString("phone"));
					dto.setAddress(rs.getString("address"));
					dto.setCity(rs.getString("city"));
					dto.setState(rs.getString("state"));
					dto.setZipcode(rs.getString("zipcode"));
					dto.setWebsite(rs.getString("website"));
					dto.setDateAdded(rs.getString("date"));
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
	public int countVendersByVendorName(String vendorName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM VENDORS WHERE LOWER(vendor_name) = LOWER(?)",new Object[] {vendorName} ,Integer.class);			
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}

	}

	@Override
	public int countVendorByEmail(String email) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM VENDORS WHERE LOWER(email) = LOWER(?)",new Object[] {email} ,Integer.class);			
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public int countVendersByVendorNameNId(Integer id, String vendorName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM VENDORS WHERE LOWER(vendor_name) = LOWER(?) AND id != ?",new Object[] {vendorName,id} ,Integer.class);			
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public int countVendorByEmailNId(Integer id, String email) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM VENDORS WHERE LOWER(email) = LOWER(?) AND id != ?",new Object[] {email , id} ,Integer.class);			
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public int updateVendorById(Integer id, VendorDTO vendor) {
		try {
			StringBuilder updateVendorQry = new StringBuilder();
			updateVendorQry.append(" UPDATE vendors ")
							.append(" SET  ")
							.append(" vendor_name=?,   ")
							.append(" company=?,  ")
							.append(" address=?,  ")
							.append(" city=?,   ")
							.append(" state=?, ")
							.append(" zipcode=?, ")
							.append(" phone=?, ")
							.append(" email=?,  ")
							.append(" 	website=? ")
							.append(" WHERE ")
							.append(" id=? ");
				  
			return jdbcTemplate.update(updateVendorQry.toString(),vendor.getVendorName(),vendor.getCompany(),vendor.getAddress(), vendor.getCity(), vendor.getState(),vendor.getZipcode(), vendor.getPhone(),vendor.getEmail(),vendor.getWebsite(), id);
		} catch (Exception e) {
			log.error("message is {} and Exception is {}", e.getMessage(), e);
			return -1;
		}
	}

<<<<<<< HEAD
	@Override
	public int deleteVendorById(Integer id) {
		try {
			return jdbcTemplate.update(" UPDATE vendors SET is_active= false WHERE id=? ",id);
		} catch (Exception e) {
			log.error("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

=======
>>>>>>> Operator roles are Updated By Dvs Mahajan
}
