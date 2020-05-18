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
import com.luvbrite.model.CreateShopDTO;
import com.luvbrite.model.PasswordDTO;
import com.luvbrite.model.ShopDTO;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IShopService;
import com.luvbrite.service.IUserService;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@NoArgsConstructor
@RequestMapping("/api/shop/")
public class ShopController {

	@Autowired
	private IShopService iShopService;
	@Autowired
	private IUserService iUserService;

	@PostMapping("/createShop")
	public ResponseEntity<CommonResponse> createShop(@RequestBody CreateShopDTO shopDTO,
			Authentication authentication) {
		CommonResponse commonResponse = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				shopDTO.setOwnerId("1");
				shopDTO.setUserTypeId("3");
				shopDTO.setCreatedBy(userDetails.getId());
				log.info("Shop DTO is {}", shopDTO);
				return validateNCreateShop(shopDTO, commonResponse);

			}
			commonResponse.setCode(401);
			commonResponse.setStatus("Unauthorized");
			commonResponse.setMessage("Please try to login and try again");
			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		} catch (Exception e) {
			commonResponse.setCode(500);
			commonResponse.setMessage("Shop is not created.please try again later.");
			commonResponse.setStatus("SERVER ERROR");
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			return new ResponseEntity<>(commonResponse, HttpStatus.OK);

		}
	}

	private ResponseEntity<CommonResponse> validateNCreateShop(CreateShopDTO shopDTO, CommonResponse commonResponse) {
		Map<String, Object> isvalidate = iShopService.validateData(shopDTO);
		if ((boolean) isvalidate.get("isValid")) {
			int addShop = iShopService.saveShop(shopDTO);
			if (addShop > 0) {
				commonResponse.setCode(201);
				commonResponse.setStatus("CREATED");
				commonResponse.setMessage("Shop Created Successfully");
				return new ResponseEntity<>(commonResponse, HttpStatus.OK);
			}
			commonResponse.setCode(400);
			commonResponse.setStatus("Bad Request");
			commonResponse.setMessage("Shop is Not Created ");
			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		} else {
			commonResponse.setCode(400);
			commonResponse.setStatus("Bad Request");
			commonResponse.setMessage("Invalid Details");
			commonResponse.setData(isvalidate);
			return new ResponseEntity<>(commonResponse, HttpStatus.OK);

		}
	}

	@GetMapping("/getAllShops")
	public ResponseEntity<CommonResponse> getAllShops() {
		CommonResponse response = new CommonResponse();
		try {
			List<ShopDTO> list = iShopService.getAllShops();
			if (!list.isEmpty()) {
				response.setCode(200);
				response.setStatus("SUCCESS");
				response.setData(list);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(400);
			response.setStatus("Bad Request");
			response.setMessage("something went wrong.please try again late");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setCode(500);
			response.setMessage("Shop is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@PutMapping("/updateShopById/{id}")
	public ResponseEntity<CommonResponse> updateShopById(@PathVariable("id") Integer id, @RequestBody ShopDTO shop) {
		CommonResponse response = new CommonResponse();
		log.info("hello {} ", shop);
		Map<String, Object> isValid = iShopService.isValidateForUpdate(id, shop);
		if ((boolean) isValid.get("isValid") == true) {
			int update = iShopService.updateShopById(id, shop);
			if (update > 0) {
				response.setCode(200);
				response.setMessage("shop data updated successfully");
				response.setStatus("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(422);
			response.setStatus("Unprocessable");
			response.setMessage("Shop data is not updated");
			return new ResponseEntity<>(response, HttpStatus.OK);

		} else {
			response.setCode(400);
			response.setStatus("Bad Request");
			response.setMessage("Invalid Details");
			response.setData(isValid);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@DeleteMapping("/deleteShopById/{id}")
	public ResponseEntity<CommonResponse> deleteShopById(@PathVariable("id") Integer id) {
		log.info("id is {}", id);
		CommonResponse response = new CommonResponse();
		try {
			int delete = iShopService.deleteShopById(id);
			if(delete>0) {
				response.setCode(200);
				response.setMessage("Shop Deleted Successfully");
				response.setStatus("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(422);
			response.setStatus("Unprocessable");
			response.setMessage("Shop is Not Deleted");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Shop is Not Deleted.Please Try Again Later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PutMapping("/updateOperatorPwdByShopId/{id}")
	public ResponseEntity<CommonResponse> updateOperatorPwdByShopId(@PathVariable("id") Integer id ,@RequestBody PasswordDTO password) {
		CommonResponse  response = new CommonResponse();
		try {
			if (password.getPassword().equals(password.getConfirmPassword())) {
				int updatePwd = iShopService.updatePwdByshopId(id, password.getPassword());
				if (updatePwd > 0) {
					response.setCode(200);
					response.setMessage("Shop Owner Password Updated Successfully");
					response.setStatus("Success");
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(422);
				response.setStatus("Unprocessable");
				response.setMessage("Shop Owner Password is Not Updated");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setCode(422);
				response.setStatus("Unprocessable");
				response.setMessage("Shop Owner Password and Confirm Password is Not Matching");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Shop Owner Password is Not Updated.Please Try Again Later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
