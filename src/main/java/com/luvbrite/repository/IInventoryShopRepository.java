package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.CategoryDTO;
import com.luvbrite.model.ShopInventoryDTO;

public interface IInventoryShopRepository {

	List<ShopInventoryDTO> getInventoryDetailsByShop(Integer shopId, String startDate);

	List<CategoryDTO> getAllCategories();
}