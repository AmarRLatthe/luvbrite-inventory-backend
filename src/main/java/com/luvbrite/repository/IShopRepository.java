package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.CreateShopDTO;
import com.luvbrite.model.ShopDTO;


public interface IShopRepository{

	int saveShop(CreateShopDTO shopDTO);

	CreateShopDTO getByShopName(String shopName);
<<<<<<< HEAD

	int countShopsByShopName(String shopName);

	List<ShopDTO> getAllShops();

	int countShopsByDomain(String domain);

	int countShopsByShopNameNId(Integer id, String shopName);

	int countShopsByDomainNId(Integer id, String domain);

	int updateShopById(Integer id, ShopDTO shop);

	int deleteShopById(Integer id);

	int updatePwdByshopId(Integer id, String password);
=======

	int countShopsByShopName(String shopName);

	List<ShopDTO> getAllShops();

	int countShopsByDomain(String domain);

	int countShopsByShopNameNId(Integer id, String shopName);

	int countShopsByDomainNId(Integer id, String domain);

	int updateShopById(Integer id, ShopDTO shop);
>>>>>>> Operator roles are Updated By Dvs Mahajan

	



	
	
	 
}
