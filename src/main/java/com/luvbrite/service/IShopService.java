package com.luvbrite.service;

import java.util.Map;

import com.luvbrite.model.CreateShopDTO;

public interface IShopService {

	int saveShop(CreateShopDTO shopDTO);

	Map<String, Object> validateData(CreateShopDTO shopDTO);

}
