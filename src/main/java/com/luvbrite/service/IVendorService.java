package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.VendorDTO;

public interface IVendorService {

	int saveVendor(VendorDTO vendor);

	List<VendorDTO> getVendorsDataByShopId(Integer shopId);

	
}
