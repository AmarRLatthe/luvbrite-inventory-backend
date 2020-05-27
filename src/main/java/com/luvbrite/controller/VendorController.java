package com.luvbrite.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
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
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				log.info("user is {}", userDetails);
				vendor.setShopId(userDetails.getShopId());
				vendor.setCreatedBy(userDetails.getId());

				return validateNCreateVendor(vendor, response);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Vendor is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	private ResponseEntity<CommonResponse> validateNCreateVendor(VendorDTO vendor, CommonResponse response) {
		Map<String, Object> isvalidate = iVendorService.validateVendor(vendor);
		if ((boolean) isvalidate.get("isValid")) {
			int addVendor = iVendorService.saveVendor(vendor);
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
	public ResponseEntity<CommonResponse> getAllVendorsByShop(Authentication authentication) {
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<VendorDTO> list = iVendorService.getVendorsDataByShopId(userDetails.getShopId());
				if (list != null) {
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
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Vendor Data not able to get.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PutMapping("/updateVendorById/{id}")
	public ResponseEntity<CommonResponse> updateVendorById(@PathVariable("id") Integer id,
			@RequestBody VendorDTO vendor) {
		CommonResponse response = new CommonResponse();
		try {
			Map<String, Object> isValid = iVendorService.validateVendorForUpdate(id, vendor);
			if ((boolean) isValid.get("isValid")) {
				int update = iVendorService.updateVendorDataById(id, vendor);
				if (update > 0) {
					response.setCode(200);
					response.setMessage("Vendor updated successfully");
					response.setStatus("Success");
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(422);
				response.setStatus("Unprocessable");
				response.setMessage("Vendor is not updated");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(400);
			response.setStatus("Bad Request");
			response.setMessage("Invalid Details");
			response.setData(isValid);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Vendor not able to get.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);

		}
	}

	@DeleteMapping("/deleteVendorById/{id}")
	public ResponseEntity<CommonResponse> deleteVendorById(@PathVariable("id") Integer id) {
		log.info("id is {}", id);

		CommonResponse response = new CommonResponse();
		try {
			int delete = iVendorService.deleteVendorById(id);
			if (delete > 0) {
				response.setCode(200);
				response.setMessage("Vendor Deleted successfully");
				response.setStatus("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(422);
			response.setStatus("Unprocessable");
			response.setMessage("Vendor is not Deleted");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Vendor is not deleted.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getAllVendorNames")
	public ResponseEntity<CommonResponse> getAllVendorNames(Authentication authentication) {
		CommonResponse commonResponse = new CommonResponse();

//		List<ProductsExt> productList = null;

		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<String[]> vendorNames = iVendorService.getAllVendorNamesByShopId(userDetails.getShopId());
				if (vendorNames != null) {
					commonResponse.setCode(200);
					commonResponse.setData(vendorNames);
					commonResponse.setMessage("Vendor names fetched successfully");
					commonResponse.setStatus("SUCCESS");

					return new ResponseEntity<>(commonResponse, HttpStatus.OK);
				}
				commonResponse.setCode(422);
				commonResponse.setStatus("Unprocessable");
				commonResponse.setMessage("Not able to fetch vendor names.");
				return new ResponseEntity<>(commonResponse, HttpStatus.OK);

			}
			commonResponse.setCode(401);
			commonResponse.setStatus("Unauthorized");
			commonResponse.setMessage("Please try to login and try again");
			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		} catch (Exception e) {
			commonResponse.setCode(500);
//			commonResponse.setData(productList);
			commonResponse.setMessage("Something went wrong.please try again later");
			commonResponse.setStatus("FAILED");
			log.error("Message is {} and exception is {}", e.getMessage(), e);

			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		}
	}
}
