package com.luvbrite.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.CategoryDTO;
import com.luvbrite.model.ShopInventoryDTO;
import com.luvbrite.repository.IInventoryShopRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryShopServiceImpl implements IInventoryShopService{
	
	@Autowired
	private IInventoryShopRepository iInventoryShopRepository;
	
	
	@Override
	public List<ShopInventoryDTO> getInventoryDetailsByShop(Integer shopId, String startDate) {
		try {
			return iInventoryShopRepository.getInventoryDetailsByShop(shopId, startDate);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<CategoryDTO> getCategories() {
		try {
			return iInventoryShopRepository.getAllCategories();
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return Collections.emptyList();
		}
	}
	
}
