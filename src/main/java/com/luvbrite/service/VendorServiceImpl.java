package com.luvbrite.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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

	@Override
	public Map<String, Object> validateVendor(VendorDTO vendor) {
		Map<String , Object> map = new HashMap<String, Object>();
		try {
			map.put("isValid", true);
			if(vendor.getVendorName()==null) {
				map.put("vendorName", "vendorName should not be empty");
				map.put("isValid", false);
			}else {
				int count = iVendorRepository.countVendersByVendorName(vendor.getVendorName());
				if(count>0) {
					map.put("vendorName", "vendorName is already available.please try with different vendorName");
					map.put("isValid", false);
				}
			}

			if(vendor.getEmail()!=null ) {
				if(StringUtils.isNotBlank(vendor.getEmail()))
				{
					int count = iVendorRepository.countVendorByEmail(vendor.getEmail());
					if(count>0) {
						map.put("email", "email is already available.please try with different email");
						map.put("isValid", false);
					}
				}
<<<<<<< HEAD
=======

>>>>>>> operator-roles updated By Dvs Mahajan
			}
			return map;
		}catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			map.put("isValid", false);
			map.put("message","Something went Wrong. please try again later.");
			return map;
		}
	}

	@Override
	public Map<String, Object> validateVendorForUpdate(Integer id, VendorDTO vendor) {
		Map<String , Object> map = new HashMap<String, Object>();
		try {
			map.put("isValid", true);
			if(vendor.getVendorName()==null) {
				map.put("vendorName", "vendorName should not be empty");
				map.put("isValid", false);
			}else {
				int count = iVendorRepository.countVendersByVendorNameNId(id,vendor.getVendorName());
				if(count>0) {
					map.put("vendorName", "vendorName is already available.please try with different vendorName");
					map.put("isValid", false);
				}
			}

			if(vendor.getEmail()!=null ) {
				if(StringUtils.isNotBlank(vendor.getEmail()))
				{
					int count = iVendorRepository.countVendorByEmailNId(id,vendor.getEmail());
					if(count>0) {
						map.put("email", "email is already available.please try with different email");
						map.put("isValid", false);
					}
				}
<<<<<<< HEAD
=======

>>>>>>> operator-roles updated By Dvs Mahajan
			}
			return map;
		}catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			map.put("isValid", false);
			map.put("message","Something went Wrong. please try again later.");
			return map;
		}
	}

	@Override
	public int updateVendorDataById(Integer id, VendorDTO vendor) {
		try {
			return iVendorRepository.updateVendorById(id,vendor);
		} catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

<<<<<<< HEAD
	@Override
	public int deleteVendorById(Integer id) {
		try {
			return iVendorRepository.deleteVendorById(id);
		} catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

=======
>>>>>>> operator-roles updated By Dvs Mahajan

}
