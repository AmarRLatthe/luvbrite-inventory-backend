package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.DriverDTO;

public interface IDriverRepository {

	int saveDriver(DriverDTO driver);

	List<DriverDTO> getDriversByShopId(Integer shopId);

	DriverDTO findByDriverName(String driverName);

	int countDriverByDriverName(String driverName);

	int updateDriverById(int id, DriverDTO driver);

	int countDriverByDriverNameNId(int id, String driverName);

<<<<<<< HEAD
	int deleteDriverById(Integer id);

=======
>>>>>>> Operator roles are Updated By Dvs Mahajan
}
