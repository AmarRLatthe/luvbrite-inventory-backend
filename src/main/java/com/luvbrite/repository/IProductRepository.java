package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.ProductDetailsDTO;

public interface IProductRepository {


	List<ProductDetailsDTO> getAllProducts();

}
