package com.luvbrite.service;

import com.luvbrite.model.ProductDetailsDTO;

public interface IProductsAvailableTableUpdate {

	
	public boolean updateProductsAvailable(ProductDetailsDTO productDetailsDTO,Integer shopId) throws Exception;
}
