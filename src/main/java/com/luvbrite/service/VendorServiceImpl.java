package com.luvbrite.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.VendorDTO;
import com.luvbrite.repository.IVendorRepository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@NoArgsConstructor
public class VendorServiceImpl implements IVendorService{

	@Autowired
	private IVendorRepository iVendorRepository;

	@Override
	public int saveVendor(VendorDTO vendor) {
		try {
			return iVendorRepository.saveVendor(vendor);			
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}

	}

	@Override
	public List<VendorDTO> getVendorsDataByShopId(Integer shopId) {
		try {
			return iVendorRepository.getVendorsDataByShopId(shopId);			
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return Collections.emptyList();
		}
	}

	
}
