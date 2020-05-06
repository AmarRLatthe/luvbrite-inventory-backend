package com.luvbrite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.DriverDTO;
import com.luvbrite.repository.IDriverRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DriverServiceImpl implements IDriverService {

	@Autowired
	private IDriverRepository iDriverRepository;
	
	@Override
	public int saveDriver(DriverDTO driver) {
		try {
			log.info("Came in driver Service ");
			return iDriverRepository.saveDriver(driver);
			
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public List<DriverDTO> getDriverDataByShopId(Integer shopId) {
		try {
			return iDriverRepository.getDriversByShopId(shopId);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return null;
		}

	}

}
