package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.DriverDTO;

public interface IDriverService {

	int saveDriver(DriverDTO driver);

	List<DriverDTO> getDriverDataByShopId(Integer shopId);

}
