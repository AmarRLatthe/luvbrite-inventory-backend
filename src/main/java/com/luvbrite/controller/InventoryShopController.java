package com.luvbrite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.CategoryDTO;
import com.luvbrite.model.ShopInventoryDTO;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IInventoryShopService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("api/shopInventory/")
public class InventoryShopController {

	@Autowired
	private IInventoryShopService iInventoryShopService;
	
	@Autowired
	private IUserService iUserService;
	
	@GetMapping("getInventoryDetails")
	public ResponseEntity<CommonResponse> getShopinventoryByShop(Authentication authentication,
			@RequestParam String startDate){
		CommonResponse response = new CommonResponse();
		try {
			UserDetails details = iUserService.getByUsername(authentication.getName());
			if(details!=null) {
				List<ShopInventoryDTO> list = iInventoryShopService.getInventoryDetailsByShop(details.getShopId(), startDate);
				response.setCode(200);
				response.setData(list);
				response.setStatus("SUCCESS");	
			}else {
				response.setCode(401);
				response.setStatus("Unauthorized");
				response.setMessage("Unauthorized user");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			response.setCode(500);
			response.setStatus("INTERNAL SERVER ERROR");
			response.setMessage("Something went wrong. please try again later");
			return new ResponseEntity<>(response,HttpStatus.OK);
		}
	}
	
	@GetMapping(value = "/getAllCategories")
	public ResponseEntity<CommonResponse> getAllCategories(Authentication authentication){
		CommonResponse response = new CommonResponse();
		try {
			UserDetails details = iUserService.getByUsername(authentication.getName());
			if(details!=null) {
				List<CategoryDTO> list = iInventoryShopService.getCategories();
				response.setCode(200);
				response.setData(list);
				response.setStatus("SUCCESS");	
			}else {
				response.setCode(401);
				response.setStatus("Unauthorized");
				response.setMessage("Unauthorized user");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			response.setCode(500);
			response.setStatus("INTERNAL SERVER ERROR");
			response.setMessage("Something went wrong. please try again later");
			return new ResponseEntity<>(response,HttpStatus.OK);
		}
	}
}
