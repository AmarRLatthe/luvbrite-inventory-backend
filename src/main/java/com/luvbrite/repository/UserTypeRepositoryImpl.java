package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.luvbrite.model.UserTypeDTO;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@NoArgsConstructor
@Slf4j
public class UserTypeRepositoryImpl implements IUserTypeRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<UserTypeDTO> getAllUserTypes() {
		log.info("usertype repository..................................................................");
		try {
			return jdbcTemplate.query("SELECT * FROM user_type_details", new RowMapper<UserTypeDTO>() {

				@Override
				public UserTypeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					UserTypeDTO dto = new UserTypeDTO();
					dto.setId(rs.getInt("id"));
					dto.setUserType(rs.getString("user_type"));

					return dto;
				}
			});
		} catch (Exception e) {
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			return null;
		}

	}

}
