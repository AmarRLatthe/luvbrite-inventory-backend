package com.luvbrite.controller;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonResponse.CommonResponse;
import com.luvbrite.model.PurchaseDTO;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IUserService;
import com.luvbrite.service.PurchaseServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PurchaseController {


	
	@Autowired
	private PurchaseServiceImpl purchaseService;

	@Autowired
	private IUserService iUserService;

	@PostMapping("/addpurchase")
	public ResponseEntity<CommonResponse> addPurchase(@RequestBody PurchaseDTO purchaseDTO,
			Authentication authentication) {
		CommonResponse commonResponse = new CommonResponse();

		UserDetails userDetails = iUserService.getByUsername(authentication.getName());

		try {
			if (userDetails != null) {
				purchaseDTO.setShopId(userDetails.getShopId());
				purchaseDTO.setCreatedBy(userDetails.getId());
				
				PurchaseDTO responsePurchaseDTO = purchaseService.addPurchase(purchaseDTO);
				
				commonResponse.setCode(201);
				commonResponse.setStatus("ADDED");
				commonResponse.setData(responsePurchaseDTO);
				commonResponse.setMessage("Purchase added successfully");
				return new ResponseEntity<>(commonResponse, HttpStatus.OK);

			} else {
				commonResponse.setCode(401);
				commonResponse.setStatus("Unauthorized");
				commonResponse.setMessage("Please try to login and try again");
				return new ResponseEntity<>(commonResponse, HttpStatus.OK);
			}
		} catch (Exception e) {
			commonResponse.setCode(500);
			commonResponse.setMessage(e.getMessage());
			commonResponse.setStatus("SERVER ERROR");
			log.error("Message is {} and exception is {}", e.getMessage());

			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		

		}

	}

	
	@GetMapping("/getpurchases")
	public ResponseEntity<CommonResponse> getPurchases(@RequestParam(value="sort",required=false) String orderBy,
			                                           @RequestParam(value="sdir",required=false) String sortDirection,
			                                           @RequestParam(value="pn",required=false)String productName,
			                                           @RequestParam(value="pc",required=false)String packetCode,
			                                           @RequestParam(value="v",required=false) int vendorId,
			                                           @RequestParam(value="sd",required=false)String startDate,
			                                           @RequestParam(value="ed",required=false)String endDate,
			                                           @RequestParam(value="src",required=false)String source,
			                                           @RequestParam(value="pid",required=false)int productId,
			                                           @RequestParam(value="adj",required=false)boolean adjustmentsOnly,
			                                           @RequestParam(value="cpage",required=false)int currentPage){
	
		
		
		
	
		purchaseService.getPurchases(orderBy,
				                     sortDirection, 
				                     productName, 
				                     packetCode, 
				                     vendorId, 
				                     startDate, 
				                     endDate, 
				                     source, 
				                     productId, 
				                     adjustmentsOnly, 
				                     currentPage);
		
	return null;
	}
	
}
