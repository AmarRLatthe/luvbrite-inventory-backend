package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.CreateShopDTO;
import com.luvbrite.repository.IShopRepository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class ShopServiceImpl implements IShopService {

	@Autowired
	private IShopRepository iShopRepository;

	@Override
	public int saveShop(CreateShopDTO shopDTO) {
		return iShopRepository.saveShop(shopDTO);
	}

}
