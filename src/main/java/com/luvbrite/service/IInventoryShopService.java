package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.CategoryDTO;
import com.luvbrite.model.ShopInventoryDTO;

public interface IInventoryShopService {

	List<ShopInventoryDTO> getInventoryDetailsByShop(Integer shopId, String startDate);
	
	List<CategoryDTO> getCategories();

}
