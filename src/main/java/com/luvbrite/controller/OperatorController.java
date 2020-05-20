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
<<<<<<< HEAD
import com.luvbrite.model.AuthoritiesDTO;
import com.luvbrite.model.PasswordDTO;
import com.luvbrite.model.PermissionDTO;
=======
>>>>>>> dipatch in progress
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
				if ((userDetails.getId() == 1) || (userDetails.getShopId() == 1)) {
					if (operator.getUserType().equals("MANAGER")) {
						operator.setUserType("MAIN MANAGER");
					}
					operator.setOwnerId(1);
				} else {
					if (userDetails.getOwnerId() == 1) {
						operator.setOwnerId(userDetails.getId());
					} else {
						operator.setOwnerId(userDetails.getOwnerId());
					}
				}
				operator.setShopId(userDetails.getShopId());
				operator.setCreatedBy(userDetails.getId());
				log.info("Operator Data is {}", operator);

				return validateNCreateOpertor(operator, response);

			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Operator is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	private ResponseEntity<CommonResponse> validateNCreateOpertor(UserDetails operator, CommonResponse response) {
		Map<String, Object> isvalidate = iOperatorService.validateOperator(operator);
		if ((boolean) isvalidate.get("isValid")) {
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
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setCode(400);
			response.setStatus("Bad Request");
			response.setMessage("Invalid Details");
			response.setData(isvalidate);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getAllOperatorsByShop")
	public ResponseEntity<CommonResponse> getAllOperatorsByShop(Authentication authentication) {
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<UserDetails> list = iOperatorService.getOperatorsDataByShopId(userDetails.getShopId());
				if ((list != null) && !list.isEmpty()) {
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
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Operator's data not able to get.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PutMapping("/updateOperatorById/{id}")
	public ResponseEntity<CommonResponse> updateOperatorById(@PathVariable("id") int id,
			@RequestBody UserDetails operator, Authentication authentication) {
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				if ((userDetails.getId() == 1) || (userDetails.getShopId() == 1)) {
					if ((id != 1) && operator.getUserType().equals("MANAGER")) {
						operator.setUserType("MAIN MANAGER");
					}
					if ((id == 1) && !operator.getUserType().equals("MAIN ADMIN")) {
						operator.setUserType("MAIN ADMIN");
					}

				}
				int update = iOperatorService.updateOperatorById(id, operator);
				if (update > 0) {
					response.setCode(200);
					response.setStatus("OK");
					response.setMessage("operator data is updated Successfully");
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(422);
				response.setStatus("Unprocessable");
				response.setMessage("Operator data is not updated");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setCode(500);
			response.setMessage("Operator is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@DeleteMapping("/deleteOperatorById/{id}")
	public ResponseEntity<CommonResponse> deleteOperatorById(@PathVariable("id") Integer id) {

		CommonResponse response = new CommonResponse();
		try {
			int delete = iOperatorService.deleteOperatorById(id);
			if(delete>0) {
				response.setCode(200);
				response.setMessage("Operator Deleted successfully");
				response.setStatus("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(422);
			response.setStatus("Unprocessable");
			response.setMessage("Operator is not Deleted");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Operator is not deleted.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@PutMapping("updateOperatorPwdById/{id}")
	public ResponseEntity<CommonResponse> updateOperatorPwdById(@PathVariable("id") Integer id,
			@RequestBody PasswordDTO password) {
		log.info("id is {}",id);
		log.info(" operator is  {}" ,password);
		CommonResponse response = new CommonResponse();
		try {
			if (password.getPassword().equals(password.getConfirmPassword())) {
				int updatePwd = iOperatorService.updatePwdByOperatorId(id, password.getPassword());
				if (updatePwd > 0) {
					response.setCode(200);
					response.setMessage("Operator password Updated successfully");
					response.setStatus("Success");
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(422);
				response.setStatus("Unprocessable");
				response.setMessage("Operator password  is not updated");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setCode(422);
				response.setStatus("Unprocessable");
				response.setMessage("Operator password and confirm is not matching");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Operator password is not updated.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@GetMapping("/getAuthoritiesNPermissions/{id}")
	public ResponseEntity<CommonResponse> getAuthoritiesNPermissions(@PathVariable("id") Integer id){
		CommonResponse response = new CommonResponse();
		try {
			String userType = iOperatorService.getUsertypeById(id);
			if("MAIN MANAGER".equals(userType)) {
				Map<String, Object> data = iOperatorService.getAuthoritiesNPermissions(id);
				response.setCode(200);
				response.setMessage("Operator password Updated successfully");
				response.setStatus("Success");
				response.setData(data);
				return new ResponseEntity<>(response,HttpStatus.OK);				
			}
			response.setCode(422);
			response.setStatus("Unprocessable");
			response.setMessage("You Can Give Authority to Only Main Manager");
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("somthing went wrong.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@PostMapping("/permissionGrant")
	public ResponseEntity<CommonResponse> permissionGrant(@RequestBody PermissionDTO permission){
		CommonResponse response = new CommonResponse();
		log.info("permission is {}",permission);
		try {
			String userType = iOperatorService.getUsertypeById(permission.getId());
			if("MAIN MANAGER".equals(userType)) {
//				Map<String, Object> data = iOperatorService.getAuthoritiesNPermissions(id);
				int isGranted = iOperatorService.permissionGrantByUserId(permission);
				if(isGranted>0) {
					response.setCode(200);
					response.setMessage("Manager Permission Updated successfully");
					response.setStatus("Success");
					return new ResponseEntity<>(response,HttpStatus.OK);									
				}
			}
			response.setCode(422);
			response.setStatus("Unprocessable");
			response.setMessage("You Can Give Authority to Only Main Manager");
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Operator password is not updated.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@PostMapping("/authoritiesGrant")
	public ResponseEntity<CommonResponse> authoritiesGrant(@RequestBody AuthoritiesDTO authorities){
		CommonResponse response = new CommonResponse();
		log.info("permission is {}",authorities);
		try {
			String userType = iOperatorService.getUsertypeById(authorities.getId());
			if("MAIN MANAGER".equals(userType)) {
				int isGranted = iOperatorService.authoritiesGrantByUserId(authorities);
//				Map<String, Object> data = iOperatorService.getAuthoritiesNPermissions(id);
				if(isGranted>0) {
					response.setCode(200);
					response.setMessage("Manager Authority Updated successfully");
					response.setStatus("Success");
//					response.setData(data);
					return new ResponseEntity<>(response,HttpStatus.OK);									
				}

			}
			response.setCode(422);
			response.setStatus("Unprocessable");
			response.setMessage("You Can Give Authority to Only Main Manager");
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Operator password is not updated.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
