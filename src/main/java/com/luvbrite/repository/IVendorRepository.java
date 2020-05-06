package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.VendorDTO;

public interface IVendorRepository {

	int saveVendor(VendorDTO vendor);

	List<VendorDTO> getVendorsDataByShopId(Integer shopId);


}
