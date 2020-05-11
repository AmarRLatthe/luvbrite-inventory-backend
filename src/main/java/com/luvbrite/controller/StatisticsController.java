package com.luvbrite.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonResponse.CommonResponse;

@RestController
@RequestMapping("/api/statistics/")
public class StatisticsController {

	@GetMapping("/getBasicStats")
	public ResponseEntity<CommonResponse> getBasicStats(Authentication authentication){
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
