package com.luvbrite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonResponse.CommonResponse;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IOperatorService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/operator/")
@Slf4j
public class OperatorController {

	@Autowired
	private IUserService iUserService;

	@Autowired
	private IOperatorService iOperatorService;

	@PostMapping("/createOperator")
	public ResponseEntity<CommonResponse> createOperator(@RequestBody UserDetails operator,
			Authentication authentication) {
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				if (userDetails.getId() == 1 || userDetails.getShopId() == 1) {
					if (operator.getUserType().equals("MANAGER")) {
						operator.setUserType("MAIN MANAGER");
						operator.setOwnerId(1);
					}
				} else {
					if (userDetails.getOwnerId() == 1) {
						operator.setOwnerId(userDetails.getId());
					} else {
						operator.setOwnerId(userDetails.getOwnerId());
					}
				}
				operator.setShopId(userDetails.getShopId());
				operator.setCreatedBy(userDetails.getId());
				log.info("Operator Data is {}",operator);
				int addOperator = iOperatorService.saveOperator(operator);
				log.info("driver saved?? {}", addOperator);
				if (addOperator > 0) {
					response.setCode(201);
					response.setStatus("CREATED");
					response.setMessage("Operator Created Successfully");
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("Operator is Not Created ");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Operator is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
