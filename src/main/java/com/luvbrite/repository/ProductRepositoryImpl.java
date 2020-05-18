package com.luvbrite.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.luvbrite.jdbcUtils.ProductsInfoDTOMapper;
import com.luvbrite.model.ProductsExt;
import com.luvbrite.service.IProductService;

@Repository
public class ProductRepositoryImpl implements IProductService {


	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<ProductsExt> listAllProducts() throws Exception {


		StringBuffer getAllProductsQuery = new StringBuffer();

		getAllProductsQuery.append("SELECT p.*, c.category_name, s.strain_name, ")
		.append("TO_CHAR(p.date_added, 'MM/dd/yyyy HH:MI AM') as date ")
		.append("FROM products p ")
		.append("JOIN  categories c ON c.id = p.category_id ")
		.append("ORDER by p.product_name");

		List<ProductsExt> productExtList = 	jdbcTemplate.query(getAllProductsQuery.toString(), new ProductsInfoDTOMapper());

		return productExtList;


	}






}
