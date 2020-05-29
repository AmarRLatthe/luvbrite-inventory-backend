package com.luvbrite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
public class PacketRepositoryImpl implements IPacketRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate;
}
