package com.luvbrite.service;

import java.util.List;
import java.util.Map;

import com.luvbrite.model.VendorDTO;

public interface IVendorService {

	int saveVendor(VendorDTO vendor);

	List<VendorDTO> getVendorsDataByShopId(Integer shopId);

	Map<String, Object> validateOperator(VendorDTO vendor);

	
}
