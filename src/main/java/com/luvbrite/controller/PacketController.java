package com.luvbrite.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.BulkPacketsCreation;
import com.luvbrite.model.PaginatedPackets;
import com.luvbrite.model.SinglePacketDTO;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IPacketService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/packets/")
public class PacketController {

	@Autowired
	private IPacketService iPacketService;
	@Autowired
	private IUserService iUserService;

	@PostMapping("/bulkPacketsCreation")
	public ResponseEntity<CommonResponse> bulkPacketsCreation(@RequestBody BulkPacketsCreation packets) {
		CommonResponse commonResponse = new CommonResponse();
		log.info("Bulk Packets Creation {}", packets);
		try {

			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception {}", e.getMessage(), e);
			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		}
	}

	@PostMapping("/createSinglePacket")
	public ResponseEntity<CommonResponse> createSinglePacket(@RequestBody SinglePacketDTO singlePacket) {
		CommonResponse response = new CommonResponse();
		try {
			log.info("Single Packet is {}", singlePacket);
			int insert = iPacketService.createSinglePacket(singlePacket);
			if (insert > 0) {
				response.setCode(201);
				response.setMessage("Packet is created successfully.");
				response.setStatus("CREATED");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Packet is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PutMapping("/editSinglePacket/{id}")
	public ResponseEntity<CommonResponse> editSinglePacket(@PathVariable("id") Integer id,
			@RequestBody SinglePacketDTO singlePacket) {
		CommonResponse response = new CommonResponse();
		log.info("Single Packet is {}", singlePacket);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/bulkUpdate/{id}")
	public ResponseEntity<CommonResponse> bulkUpdate(@PathVariable("id") Integer id,
			@RequestBody HashMap<String, Object> bulk) {
		CommonResponse response = new CommonResponse();
		log.info("Bulk Packet is {}", bulk);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("getPacketsList")
	public ResponseEntity<CommonResponse> getPacketsList(
			@RequestParam(value = "cpage", required = false) Integer currentPage,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "sdir", required = false) String sortDirection,
			@RequestParam(value = "p", required = false) Integer purchaseId,
			@RequestParam(value = "pc", required = false) String packetCode,
			@RequestParam(value = "all", required = false) Boolean all,
			@RequestParam(value = "sp", required = false) Integer sp,
			@RequestParam(value = "ns", required = false) Boolean notSold,
			@RequestParam(value = "sold", required = false) Boolean sold,
			@RequestParam(value = "in", required = false) Boolean inShop,
			@RequestParam(value = "ib", required = false) Boolean inShops,
			@RequestParam(value = "mis", required = false) String allmisc,
			@RequestParam(value = "si", required = false) Integer salesId,
			@RequestParam(value = "shopId", required = false) Integer shopId, Authentication authentication) {

		CommonResponse response = new CommonResponse();


		currentPage = currentPage == null ? 0 : currentPage;
		sort = sort == null ? "" : sort;
		sortDirection = sortDirection == null ? "" : sortDirection;
		purchaseId = purchaseId == null ? 0 : purchaseId;
		packetCode = packetCode == null ? "" : packetCode;
		sp = sp == null ? 0 : sp;
		all = all == null ? false : all;
		notSold = notSold == null ? false : notSold;
		sold = sold == null ? false : sold;
		inShop = inShop == null ? false : inShop;
		inShops = inShops == null ? false : inShops;
		salesId = salesId == null ? 0 : salesId;
		shopId = shopId == null ? 0 : shopId;

		PaginatedPackets paginatedPackets = null;

		if (shopId == 0) {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			shopId = userDetails.getShopId();
		}


		try {

			paginatedPackets = iPacketService.listPackets(purchaseId, salesId, shopId, notSold, sold, all, sort, sortDirection,
					packetCode, allmisc, currentPage);


			if(paginatedPackets==null) {throw new NullPointerException();}

			response.setCode(200);
			response.setData(paginatedPackets);
			response.setMessage("List of packets fetched successfully");
			response.setStatus("SUCCESS");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);

		}catch(Exception e) {

			log.error("Exception occurred while fetching paginated packets {} ",e);

			response.setCode(500);
			response.setData(paginatedPackets);
			response.setMessage("could not fetch list of packets");
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
		}


	}

}
