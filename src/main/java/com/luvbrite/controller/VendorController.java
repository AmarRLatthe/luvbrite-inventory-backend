package com.luvbrite.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonResponse.CommonResponse;
import com.luvbrite.model.UserDetails;
import com.luvbrite.model.VendorDTO;
import com.luvbrite.service.IUserService;
import com.luvbrite.service.IVendorService;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/vendor/")
@Slf4j
@NoArgsConstructor
public class VendorController {

	@Autowired
	private IUserService iUserService;
	@Autowired
	private IVendorService iVendorService;
	
	@PostMapping("/createVendor")
	public ResponseEntity<CommonResponse> createVendor(@RequestBody VendorDTO vendor, Authentication authentication) {
		CommonResponse response  = new CommonResponse();
		try {
			log.info("came in vendor Controller.....");
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());	
			if(userDetails!=null) {
				log.info("user is {}",userDetails);
				vendor.setShopId(userDetails.getShopId());
				vendor.setCreatedBy(userDetails.getId());
				
				return validateNCreateVendor(vendor, response);		
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}"+e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Driver is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}


	private ResponseEntity<CommonResponse> validateNCreateVendor(VendorDTO vendor, CommonResponse response) {
		Map<String, Object> isvalidate = iVendorService.validateOperator(vendor);
		if ((boolean) isvalidate.get("isValid")) {
			int addVendor=iVendorService.saveVendor(vendor);
			if (addVendor > 0) {
				response.setCode(201);
				response.setStatus("CREATED");
				response.setMessage("Vendor Created Successfully");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(400);
			response.setStatus("Bad Request");
			response.setMessage("Vendor is Not Created ");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setCode(400);
			response.setStatus("Bad Request");
			response.setMessage("Invalid Details");
			response.setData(isvalidate);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	
	@GetMapping("/getAllVendorByShop")
	public ResponseEntity<CommonResponse> getAllVendorsByShop(Authentication authentication){
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<VendorDTO> list = iVendorService.getVendorsDataByShopId(userDetails.getShopId());
				if(list!=null && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("something went wrong.please try again late");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}"+e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Vendor Data not able to get.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
