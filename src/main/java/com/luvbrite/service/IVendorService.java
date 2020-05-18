package com.luvbrite.service;

import java.util.List;
import java.util.Map;

import com.luvbrite.model.VendorDTO;

public interface IVendorService {

	int saveVendor(VendorDTO vendor);

	List<VendorDTO> getVendorsDataByShopId(Integer shopId);

	Map<String, Object> validateVendor(VendorDTO vendor);

	Map<String, Object> validateVendorForUpdate(Integer id, VendorDTO vendor);

	int updateVendorDataById(Integer id, VendorDTO vendor);

	
}
