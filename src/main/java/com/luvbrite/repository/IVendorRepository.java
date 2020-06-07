package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.VendorDTO;

public interface IVendorRepository {

	int saveVendor(VendorDTO vendor);

	List<VendorDTO> getVendorsDataByShopId(Integer shopId);

	VendorDTO findByVendorName(String vendorName);

	int countVendersByVendorName(String vendorName);

	int countVendorByEmail(String email);

	int countVendersByVendorNameNId(Integer id, String vendorName);

	int countVendorByEmailNId(Integer id, String email);

	int updateVendorById(Integer id, VendorDTO vendor);

	int deleteVendorById(Integer id);

	List<String[]> getAllVendorNamesByShopId(int shopId);






}
