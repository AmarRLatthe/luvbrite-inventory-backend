package com.luvbrite.repository;

import com.luvbrite.model.CreateShopDTO;


public interface IShopRepository{

	int saveShop(CreateShopDTO shopDTO);



	CreateShopDTO getByShopName(String shopName);



	int countShopsByShopName(String shopName);

	



	
	
	 
}
