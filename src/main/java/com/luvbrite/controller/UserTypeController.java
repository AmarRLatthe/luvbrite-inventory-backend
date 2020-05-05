package com.luvbrite.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonResponse.CommonResponse;
import com.luvbrite.model.UserTypeDTO;
import com.luvbrite.service.IUserTypeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/userType/")
@Slf4j
public class UserTypeController {

	@Autowired
	private IUserTypeService iUserTypeService;
	private static List<String> USER_TYPE_LIST = new ArrayList<>();
	
	
	public UserTypeController() {
		USER_TYPE_LIST.add("MAIN ADMIN");
		USER_TYPE_LIST.add("MAIN MANAGER");
		USER_TYPE_LIST.add("SHOP ADMIN");
	}

	@GetMapping("getAllUserType")
	public ResponseEntity<CommonResponse> getAllUserType(){
		List<UserTypeDTO> userTypes = iUserTypeService.getAllUserTypes(); 
		userTypes = userTypes.stream().filter(userType->!USER_TYPE_LIST.contains(userType.getUserType())).collect(Collectors.toList());
		CommonResponse commonResponse = new CommonResponse();
		commonResponse.setCode(200);
		commonResponse.setData(userTypes);
		commonResponse.setStatus("SUCCESS");
		log.info("common response is {}",commonResponse);
		return new ResponseEntity<>(commonResponse,HttpStatus.OK);
	}
}
