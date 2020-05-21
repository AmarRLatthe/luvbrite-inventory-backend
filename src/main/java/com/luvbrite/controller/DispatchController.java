package com.luvbrite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.DispatchSalesExt;
import com.luvbrite.model.DispatchUpdateDTO;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.DispatchService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController("/api/")
public class DispatchController {

	@Autowired
	DispatchService dispatchService;

	@Autowired
	private IUserService iUserService;

	@GetMapping("listdispatches")
	public ResponseEntity<CommonResponse> listdispatches(@RequestParam(value = "d", required = true) Integer driverId,
			@RequestParam(value = "id", required = false) Integer dispatchId,
			@RequestParam(value = "ca", required = false) Boolean cancelled,
			@RequestParam(value = "fn", required = false) Boolean finished,
			@RequestParam(value = "nf", required = false) Boolean notFinished,
			@RequestParam(value = "q", required = true) String q,
			@RequestParam(value = "sort", required = false) String orderBy,
			@RequestParam(value = "mode", required = false) String mode,
			@RequestParam(value = "sdir", required = false) String qSORTDIR,
			@RequestParam(value = "cpage", required = true) Integer currentPage,
			@RequestParam(value = "deliveryRtId", required = false) Integer deliveryRtId,
			Authentication authentication) {

		CommonResponse commonResponse = new CommonResponse();

		UserDetails userDetails = iUserService.getByUsername(authentication.getName());

		if (userDetails == null) {
			commonResponse.setCode(401);
			commonResponse.setData(null);
			commonResponse.setMessage("Please login to access dispatches");
			commonResponse.setStatus("FAILED");
			return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);
		}
		List<DispatchSalesExt> dispatches = null;

		int shopId = userDetails.getShopId();

		try {

			dispatches = dispatchService.listDispatches(driverId, dispatchId, cancelled, finished, notFinished, q,
					orderBy, mode, qSORTDIR, currentPage, deliveryRtId, shopId);

			if (dispatches != null) {
				commonResponse.setCode(200);
				commonResponse.setData(dispatches);
				commonResponse.setMessage("Dispatces retrieved successfully");
				commonResponse.setStatus("SUCCESS");

				return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);

			} else {

				commonResponse.setCode(500);
				commonResponse.setData(dispatches);
				commonResponse.setMessage("Dispatches could not be  retrieved");
				commonResponse.setStatus("FAILED");
				return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);
			}

		} catch (Exception e) {
			commonResponse.setCode(500);
			commonResponse.setData(dispatches);
			commonResponse.setMessage("Exception occured while retrieving  dispatches" + e.getMessage());
			commonResponse.setStatus("FAILED");
			return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);
		}

	}

	@PostMapping("/updatedisptach")
	public ResponseEntity<CommonResponse> updateDispatch(DispatchUpdateDTO dispatchUpdateDTO,
			Authentication authentication) {

		CommonResponse commonResponse = new CommonResponse();

		UserDetails userDetails = iUserService.getByUsername(authentication.getName());
		dispatchUpdateDTO.setOpsId(userDetails.getId());
		boolean success = false;
		if (userDetails == null) {
			commonResponse.setCode(401);
			commonResponse.setData(null);
			commonResponse.setMessage("Please login to access dispatches");
			commonResponse.setStatus("FAILED");
			return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);
		}

		dispatchUpdateDTO.setShopId(userDetails.getShopId());

		if (dispatchUpdateDTO.getMode().equals("basic")) {
			return dispatchService.updatePacketInfo(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("assigndrv")) {
			return  dispatchService.assignDriver(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("cancelled")) {
			return  dispatchService.cancelDispatch(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("arrived")) {
			return  dispatchService.markArrived(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("sold")) {
			commonResponse = dispatchService.markSold(dispatchUpdateDTO);
			String resp = inOfficeOrderProcess(dispatchUpdateDTO);
			if (resp.equals("Y")) {
				message = "Order Loopback created";
			}
		} else if (dispatchUpdateDTO.getMode().equals("dateupdate")) {
			commonResponse = dispatchService.dateUpdate(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("pmtmodeupdate")) {
			commonResponse = dispatchService.pmtModeUpdate(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("tipupdate")) {
			commonResponse = dispatchService.tipUpdate(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("splitupdate")) {
			commonResponse = dispatchService.splitUpdate(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("recal_dist")) {
			commonResponse = dispatchService.recalculateDistance(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("closesales")) {
			commonResponse = dispatchService.closeTheseSales(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("reopensales")) {
			commonResponse = dispatchService.reopenTheseSales(dispatchUpdateDTO);
		} else if (dispatchUpdateDTO.getMode().equals("reset")) {
			success = dispatchService.resetSale(dispatchUpdateDTO);
		}

		return null;

	}

}
