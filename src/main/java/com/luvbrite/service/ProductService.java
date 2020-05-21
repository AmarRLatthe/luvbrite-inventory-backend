package com.luvbrite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.ProductsExt;
import com.luvbrite.repository.ProductRepositoryImpl;

@Service
public class ProductService implements IProductService {

	@Autowired
	ProductRepositoryImpl productsRepo;

	@Override
	public List<ProductsExt> listAllProducts() throws Exception {

		return productsRepo.listAllProducts();

	}

}
