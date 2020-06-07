package com.luvbrite.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.ProductsExt;
import com.luvbrite.repository.IProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService implements IProductService {

	@Autowired
	IProductRepository iProductRepository;

	@Override
	public List<ProductsExt> listAllProducts() throws Exception {

		return iProductRepository.getAllProducts();

	}

	@Override
	public List<String[]> getAllProdNames() {
		try {
			return iProductRepository.getAllProdNames();
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return Collections.emptyList();
		}
	}

}
