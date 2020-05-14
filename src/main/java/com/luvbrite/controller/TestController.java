package com.luvbrite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonResponse.CommonResponse;
import com.luvbrite.service.MasterInventoryService;

@RestController
public class TestController {

@Autowired
MasterInventoryService masterService;
	
	@GetMapping("/test/api")
	public ResponseEntity<CommonResponse> TestApi(){
		
		//masterService.updateProductsAvailable(37337, 0);
		return null;
		
	}
	
	
}
