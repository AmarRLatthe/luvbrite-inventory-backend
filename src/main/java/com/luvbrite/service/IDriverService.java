package com.luvbrite.service;

import java.util.List;
import java.util.Map;

import com.luvbrite.model.DriverDTO;

public interface IDriverService {

	int saveDriver(DriverDTO driver);

	List<DriverDTO> getDriverDataByShopId(Integer shopId);

	Map<String, Object> validateDriver(DriverDTO driver);

	Map<String, Object> isValidateForUpdate(int id, DriverDTO driver);

	int updateDriverById(int id, DriverDTO driver);

	int deleteDriverById(Integer id);

}
