package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.ProductsExt;

public interface IProductService {

	public List<ProductsExt> listAllProducts() throws Exception;

	public List<String[]> getAllProdNames();
}
