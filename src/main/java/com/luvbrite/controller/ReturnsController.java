package com.luvbrite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IPacketService;
import com.luvbrite.service.IReturnsService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/return/")
@Slf4j
public class ReturnsController {

	@Autowired
	private IReturnsService returnService;

	@Autowired
	private IUserService iUserService;

	@Autowired
	private IPacketService iPacketService;

	@GetMapping("/listreturns")
	ResponseEntity<CommonResponse> listReturns() {

		return null;

	}

	ResponseEntity<CommonResponse> addReturn(@RequestParam(value = "pc", required = false) String packetCode,
			@RequestParam(value = "reason", required = false) String reason, Authentication authentication) {

		CommonResponse response = new CommonResponse();

		UserDetails userDetails = iUserService.getByUsername(authentication.getName());

		// Validation
		if (((packetCode == null) || packetCode.equals("")) || ((reason == null) || reason.equals(""))) {

			response.setCode(400);
			response.setData(false);
			response.setStatus("FAILED");
			response.setMessage("Invalid Packet Code, reason");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);

		}

		try {
			if (userDetails != null) {

				int shopId = userDetails.getShopId();
				int operatorId = userDetails.getId();

				if(!iPacketService.checkIfValidBarcode(packetCode, shopId)) {

					response.setCode(200);
					response.setData(false);
					response.setMessage(packetCode+"is not a valid barcode");
					response.setStatus("SUCCESS");

					return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
				}

				int returnstatus = returnService.addReturn(packetCode, reason, shopId, operatorId);

				if (returnstatus == 0) {

					response.setCode(200);
					response.setData(false);
					response.setMessage("Packet Code " + packetCode + "already marked as returned ");
					response.setStatus("SUCCESS");
				} else if (returnstatus == 1) {
					response.setCode(200);
					response.setData(true);
					response.setMessage("Packet Code " + packetCode + " marked as returned successfully");
					response.setStatus("SUCCESS");
				} else {
					response.setCode(200);
					response.setData(true);
					response.setMessage("Packet " + packetCode + " already returned ");
					response.setStatus("FAILED");
				}

			} else {

				response.setCode(401);
				response.setData("");
				response.setMessage("Accessing user does not exists ");
				response.setStatus("FAILURE");
			}
			return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
		} catch (Exception e) {

			log.error("Exception occured while returning packet : " + packetCode, e);
			response.setCode(500);
			response.setData(false);
			response.setMessage("Exception occured while returning packet : " + packetCode);
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
		}

	}

}
