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
import com.luvbrite.model.DriverDTO;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IDriverService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/driver/")
@Slf4j
public class DriverController {

	@Autowired
	private IUserService iUserService;
	@Autowired
	private IDriverService iDriverService;

	@PostMapping("/createDriver")
	public ResponseEntity<CommonResponse> createDriver(@RequestBody DriverDTO driver, Authentication authentication) {
		CommonResponse response = new CommonResponse();
		try {
			log.info("came in create driver");
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			log.info("user details is {}", userDetails);
			if (userDetails != null) {
				driver.setShopId(userDetails.getShopId());
				driver.setCreatedBy(userDetails.getId());
				return validateNSaveDriver(driver, response);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Driver is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	private ResponseEntity<CommonResponse> validateNSaveDriver(DriverDTO driver, CommonResponse response) {
		Map<String, Object> isvalidate = iDriverService.validateOperator(driver);
		if ((boolean) isvalidate.get("isValid")) {
			if(driver.getUserName()==null) {
				driver.setUserName(driver.getDriverName());
			}
			int addDriver = iDriverService.saveDriver(driver);
			log.info("driver saved?? {}", addDriver);
			if (addDriver > 0) {
				response.setCode(201);
				response.setStatus("CREATED");
				response.setMessage("Driver Created Successfully");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(400);
			response.setStatus("Bad Request");
			response.setMessage("Driver is Not Created ");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setCode(400);
			response.setStatus("Bad Request");
			response.setMessage("Invalid Details");
			response.setData(isvalidate);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getAllDriverByShop")
	public ResponseEntity<CommonResponse> getAllDriverByShop(Authentication authentication) {
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<DriverDTO> list = iDriverService.getDriverDataByShopId(userDetails.getShopId());
				if (list != null && !list.isEmpty()) {
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
			response.setMessage("Drivers Data not able to get.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
