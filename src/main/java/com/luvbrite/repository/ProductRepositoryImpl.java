package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.luvbrite.jdbcUtils.ProductsInfoDTOMapper;
import com.luvbrite.model.ProductsExt;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ProductRepositoryImpl implements IProductRepository {


	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<ProductsExt> getAllProducts() {


		StringBuffer getAllProductsQuery = new StringBuffer();

		getAllProductsQuery.append("SELECT p.*, c.category_name, s.strain_name, ")
		.append("TO_CHAR(p.date_added, 'MM/dd/yyyy HH:MI AM') as date ")
		.append("FROM products p ")
		.append("JOIN  categories c ON c.id = p.category_id ")
		.append("ORDER by p.product_name");
		
		log.info("product query is {}",getAllProductsQuery);
		List<ProductsExt> productExtList = 	jdbcTemplate.query(getAllProductsQuery.toString(), new ProductsInfoDTOMapper());

		return productExtList;


	}

	@Override
	public List<String[]> getAllProdNames() {
		try {
			return jdbcTemplate.query("SELECT id,product_name FROM products ORDER BY product_name ASC ", new RowMapper<String[]>() {

				@Override
				public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
					String[] st = new String[2];
					st[0]= rs.getString(1);
					st[1]= rs.getString(2);		
					return st;
				}});
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(), e);
			return Collections.emptyList();
		}
	}






}
