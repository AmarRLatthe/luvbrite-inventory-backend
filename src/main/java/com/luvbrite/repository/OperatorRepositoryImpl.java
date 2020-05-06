package com.luvbrite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
					.append(" ( email, password, username, owner_id, shop_id, user_type_id,  created_by) ")
					.append(" VALUES (?, ?, ?, ?, ?, (SELECT id FROM user_type_details WHERE user_type= ?), ?) ");
			return jdbcTemplate.update(saveOperatorQry.toString(), operator.getEmail(),
					"{bcrypt}"+bCryptPasswordEncoder.encode(operator.getPassword()), operator.getUsername(), operator.getOwnerId(),
					operator.getShopId(), operator.getUserType(), operator.getCreatedBy());
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return -1;
		}
		
	}

}
