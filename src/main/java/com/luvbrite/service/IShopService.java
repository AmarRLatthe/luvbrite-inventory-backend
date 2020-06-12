package com.luvbrite.service;

import java.util.List;
import java.util.Map;

import com.luvbrite.model.CreateShopDTO;
import com.luvbrite.model.ShopDTO;

public interface IShopService {

	int saveShop(CreateShopDTO shopDTO);

	Map<String, Object> validateData(CreateShopDTO shopDTO);

	List<ShopDTO> getAllShops();

	Map<String, Object> isValidateForUpdate(Integer id, ShopDTO shop);

	int updateShopById(Integer id, ShopDTO shop);

	int deleteShopById(Integer id);

	int updatePwdByshopId(Integer id, String password);

	List<ShopDTO> getShopListByManagerId(Integer id);

}
