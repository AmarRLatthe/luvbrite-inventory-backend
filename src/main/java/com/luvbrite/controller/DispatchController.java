package com.luvbrite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.DispatchUpdateDTO;
import com.luvbrite.model.PaginatedDispatch;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IDispatchService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/dispatch/")
public class DispatchController {

	@Autowired
	private	IDispatchService dispatchServiceImpl;

	@Autowired
	private IUserService iUserService;

	@GetMapping("listdispatches")
	public ResponseEntity<CommonResponse> listdispatches(
			@RequestParam(value = "d", required = false) Integer driverId,
			@RequestParam(value = "id", required = false) Integer dispatchId,
			@RequestParam(value = "ca", required = false) Boolean cancelled,
			@RequestParam(value = "fn", required = false) Boolean finished,
			@RequestParam(value = "nf", required = false) Boolean notFinished,
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "sort", required = false) String orderBy,
			@RequestParam(value = "mode", required = false) String mode,
			@RequestParam(value = "sdir", required = false) String qSORTDIR,
			@RequestParam(value = "cpage", required = false) Integer currentPage,
			@RequestParam(value = "deliveryRtId", required = false) Integer deliveryRtId,
			Authentication authentication) {

		CommonResponse commonResponse = new CommonResponse();

		UserDetails userDetails = iUserService.getByUsername(authentication.getName());

		if (userDetails == null) {
			commonResponse.setCode(401);
			//commonResponse.setData(null);
			commonResponse.setMessage("Please login to access dispatches");
			commonResponse.setStatus("FAILED");
			return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);
		}
		PaginatedDispatch dispatches = null;

		int shopId = userDetails.getShopId();

		try {

			dispatches = dispatchServiceImpl.listDispatches(driverId, dispatchId, cancelled, finished, notFinished, q,
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

		if (userDetails == null) {
			commonResponse.setCode(401);
			commonResponse.setData(null);
			commonResponse.setMessage("Please login to access dispatches");
			commonResponse.setStatus("FAILED");
			return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);
		}

		int operatorId = userDetails.getId();
		int shopId =   userDetails.getShopId();

		dispatchUpdateDTO.setOpsId(userDetails.getId());



		dispatchUpdateDTO.setShopId(userDetails.getShopId());

		try {

			if (dispatchUpdateDTO.getMode().equals("basic")) {
				return dispatchServiceImpl.updatePacketInfo(dispatchUpdateDTO);
			} else if (dispatchUpdateDTO.getMode().equals("assigndrv")) {
				return  dispatchServiceImpl.assignDriver(dispatchUpdateDTO);
			} else if (dispatchUpdateDTO.getMode().equals("cancelled")) {
				return  dispatchServiceImpl.cancelDispatch(dispatchUpdateDTO);
			} else if (dispatchUpdateDTO.getMode().equals("arrived")) {
				return  dispatchServiceImpl.markArrived(dispatchUpdateDTO,shopId);
			} else if (dispatchUpdateDTO.getMode().equals("sold")) {
				return  dispatchServiceImpl.markSold(dispatchUpdateDTO,operatorId,shopId);
				//			String resp = inOfficeOrderProcess(dispatchUpdateDTO);
				//			if (resp.equals("Y")) {
				//				message = "Order Loopback created";
				//			}
			} else if (dispatchUpdateDTO.getMode().equals("dateupdate")) {
				return  dispatchServiceImpl.dateUpdate(dispatchUpdateDTO,shopId,operatorId);
			} else if (dispatchUpdateDTO.getMode().equals("pmtmodeupdate")) {
				return dispatchServiceImpl.pmtModeUpdate(dispatchUpdateDTO,shopId,operatorId);
			} else if (dispatchUpdateDTO.getMode().equals("tipupdate")) {
				return dispatchServiceImpl.tipUpdate(dispatchUpdateDTO,shopId,operatorId);
			} else if (dispatchUpdateDTO.getMode().equals("splitupdate")) {
				return  dispatchServiceImpl.splitUpdate(dispatchUpdateDTO,shopId,operatorId);
			} else if (dispatchUpdateDTO.getMode().equals("recal_dist")) {
				//	commonResponse = dispatchServiceImpl.recalculateDistance(dispatchUpdateDTO);
			} else if (dispatchUpdateDTO.getMode().equals("closesales")) {
				//	commonResponse = dispatchServiceImpl.closeTheseSales(dispatchUpdateDTO);
			} else if (dispatchUpdateDTO.getMode().equals("reopensales")) {
				//	return  dispatchServiceImpl.reopenTheseSales(dispatchUpdateDTO);
			} else if (dispatchUpdateDTO.getMode().equals("reset")) {
				return  dispatchServiceImpl.resetSale(dispatchUpdateDTO,shopId,operatorId);
			}

			return null;
		}
		catch(Exception e) {
			commonResponse.setCode(500);
			commonResponse.setData(null);
			commonResponse.setMessage("Ooops !!! something went wrong please contact developers");
			commonResponse.setStatus("FAILED");
			log.error("Exception occured while updating dispatch ",e);
			return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);
		}
	}

}
