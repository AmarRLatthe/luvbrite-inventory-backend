package com.luvbrite.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonResponse.CommonResponse;
import com.luvbrite.model.CreateShopDTO;
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
<<<<<<< HEAD
				return validateNCreateShop(shopDTO, commonResponse);
=======
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
				return new ResponseEntity<>(commonResponse,HttpStatus.OK);
>>>>>>> PurchaseController modified
			}
			commonResponse.setCode(401);
			commonResponse.setStatus("Unauthorized");
			commonResponse.setMessage("Please try to login and try again");
			return new ResponseEntity<>(commonResponse,HttpStatus.OK);
		} catch (Exception e) {
			commonResponse.setCode(500);
			commonResponse.setMessage("Shop is not created.please try again later.");
			commonResponse.setStatus("SERVER ERROR");
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return new ResponseEntity<>(commonResponse,HttpStatus.OK);
<<<<<<< HEAD
		}
	}

	private ResponseEntity<CommonResponse> validateNCreateShop(CreateShopDTO shopDTO, CommonResponse commonResponse) {
		Map<String, Object> isvalidate = iShopService.validateData(shopDTO);
		if((boolean) isvalidate.get("isValid")) {
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
		}else {
			commonResponse.setCode(400);
			commonResponse.setStatus("Bad Request");
			commonResponse.setMessage("Invalid Details");
			commonResponse.setData(isvalidate);
			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
=======
>>>>>>> PurchaseController modified
		}
	}
}
