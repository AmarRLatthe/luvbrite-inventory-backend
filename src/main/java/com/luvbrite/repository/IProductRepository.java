package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.ProductsExt;

public interface IProductRepository {


	List<ProductsExt> getAllProducts();

	List<String[]> getAllProdNames();

}
