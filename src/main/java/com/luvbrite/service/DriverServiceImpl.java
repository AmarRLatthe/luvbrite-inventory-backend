package com.luvbrite.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Override
	public Map<String, Object> validateOperator(DriverDTO driver) {
		Map<String , Object> map = new HashMap<String, Object>();
		try {
			map.put("isValid", true);	
			if(driver.getDriverName()==null) {
				map.put("driverName", "driverName should not be empty");
				map.put("isValid", false);
			}else {
//				DriverDTO  driverDTO = iDriverRepository.findByDriverName(driver.getDriverName());
				int count = iDriverRepository.countDriverByDriverName(driver.getDriverName());
				if(count>0) {
					map.put("driverName", "driverName is already available.please try with different driverName");
					map.put("isValid", false);
				}
			}
			return map;
		}catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			map.put("isValid", false);
			map.put("message","Something went Wrong. please try again later.");
			return map;
		}
	}

}
