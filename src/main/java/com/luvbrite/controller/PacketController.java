package com.luvbrite.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.luvbrite.model.SinglePacketDTO;
import com.luvbrite.service.IPacketService;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@NoArgsConstructor
@Slf4j
@RequestMapping("/api/packets/")
public class PacketController {

	@Autowired
	private IPacketService iPacketService;
	
	@PostMapping("/bulkPacketsCreation")
	public ResponseEntity<CommonResponse> bulkPacketsCreation(@RequestBody BulkPacketsCreation packets){
		CommonResponse commonResponse = new CommonResponse();
		log.info("Bulk Packets Creation {}",packets);
		try {
			
			return new  ResponseEntity<>(commonResponse,HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception {}",e.getMessage(),e);
			return new  ResponseEntity<>(commonResponse,HttpStatus.OK);
		}
	}
	
	@PostMapping("/createSinglePacket")
	public ResponseEntity<CommonResponse> createSinglePacket(@RequestBody SinglePacketDTO singlePacket){
		CommonResponse response = new CommonResponse();
		try {
			log.info("Single Packet is {}",singlePacket);
			int insert =iPacketService.createSinglePacket(singlePacket);
			if(insert>0) {
				response.setCode(201);
				response.setMessage("Packet is created successfully.");
				response.setStatus("CREATED");
			}
			return new ResponseEntity<>(response,HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			response.setCode(500);
			response.setMessage("Packet is not created.please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@PutMapping("/editSinglePacket/{id}")
	public ResponseEntity<CommonResponse> editSinglePacket(@PathVariable("id") Integer id ,@RequestBody SinglePacketDTO singlePacket){
		CommonResponse response = new CommonResponse();
		log.info("Single Packet is {}",singlePacket);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@PutMapping("/bulkUpdate/{id}")
	public ResponseEntity<CommonResponse> bulkUpdate(@PathVariable("id") Integer id,@RequestBody HashMap<String, Object> bulk){
		CommonResponse response = new CommonResponse();
		log.info("Bulk Packet is {}",bulk);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@GetMapping("getPacketsList")
	public ResponseEntity<CommonResponse> getPacketsList(
			@RequestParam(value = "cpage", required = false) Integer currentPage, 
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "sdir", required = false) String sortDirection,
			@RequestParam(value = "p", required = false) Integer purchaseId,
			@RequestParam(value = "pc", required = false) String packetCode,
			@RequestParam(value = "all", required = false) boolean all,
			@RequestParam(value = "sp", required = false) Integer sp){
		
		CommonResponse response = new CommonResponse();
		log.info("i came here..............................................");
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
}
