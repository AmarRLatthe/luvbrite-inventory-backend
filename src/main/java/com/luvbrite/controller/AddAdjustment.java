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
import com.luvbrite.model.ChangeTrackerDTO;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.AddMiscPackets;
import com.luvbrite.service.AddPacketsAfterSales;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/addadjustment")
public class AddAdjustment {

	@Autowired
	private IUserService iUserService;


	@Autowired
	private AddPacketsAfterSales addPacketsAfterSales;


	@Autowired
	private AddMiscPackets addMiscPackets;

	@GetMapping("/adjust")
	public ResponseEntity<CommonResponse> addAdjustment(@RequestParam(value = "salesid" ,required=false)Integer salesId,
			@RequestParam(value="amount",required=false)String amount,
			@RequestParam(value="notes",required=false) String notes,
			@RequestParam(value="saleDate",required=false) String saleDate,
			Authentication authentication){

		UserDetails userDetails = iUserService.getByUsername(authentication.getName());


		CommonResponse response = new CommonResponse();

		int operatorId = userDetails.getId();
		int shopId =   userDetails.getShopId();
		int packetId =0;
		boolean miscPacketUpdateStatus=false;


		salesId = salesId == null?0:salesId;
		amount=amount==null?"":amount;
		notes=notes==null?"":notes;
		saleDate= saleDate==null?"":saleDate;
		Double amount2 = Double.valueOf(amount.trim());

		if((salesId==0) || (operatorId==0) || ((amount2==0) && (notes.trim().length()!=8))
				|| saleDate.equals("") || notes.equals("")){

			log.error("Invalid adjustment parameters");

			response.setCode(400);
			response.setMessage("Invalid adjustment parameters");
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
		}



		//This is not an adjustment, but an actual packet
		if((notes.trim().length()==8) &&
				((notes.charAt(0)=='P') || (notes.charAt(0)=='L')) &&
				!notes.equals("PROMOCPN")){

			packetId = 	addPacketsAfterSales.addPackets(notes, saleDate, amount2, operatorId, salesId, shopId);


			if(packetId==0) {
				response.setCode(500);
				response.setData(false);
				response.setMessage("Could not add coupons !!!");
				response.setStatus("FAILED");

			}else {
				response.setData(true);
				response.setMessage("coupons added successfully !!!");
				response.setStatus("SUCCESS");
				response.setCode(200);
			}


		}
		else {

			miscPacketUpdateStatus	=	addMiscPackets.addPackets(notes, saleDate, amount2, operatorId, salesId, shopId);

			if(!miscPacketUpdateStatus) {
				response.setCode(500);
				response.setData(false);
				response.setMessage("Could not add miscellaneous packets !!!");
				response.setStatus("FAILED");

			}else {
				response.setCode(200);
				response.setData(true);
				response.setMessage("Added miscellaneous packets successfully !!!");
				response.setStatus("SUCCESS");

			}
		}



		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("date_sold, sales_id, selling_price  updated to " + saleDate + ", " + salesId + ", "
				+ (amount2 == 0 ? "Marked Price" : amount));
		ct.setActionType("update");
		ct.setActionOn("packet");
		ct.setItemId(packetId);
		ct.setOperatorId(operatorId);


		return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);



	}


	/*
	 * public String execute(){ success = false;
	 *
	 *
	 * int id = routine.getInteger(req.getParameter("id")); int opsId =
	 * routine.getInteger(req.getParameter("opsid")); double amount =
	 * routine.getDouble(req.getParameter("a")); String notes =
	 * req.getParameter("r")==null?"":req.getParameter("r"); String saleDate =
	 * req.getParameter("d")==null?"":req.getParameter("d");
	 *
	 *
	 * if(id==0 || opsId==0 || (amount==0 && notes.trim().length()!=8) ||
	 * saleDate.equals("") || notes.equals("")){ message =
	 * "Invalid adjustment parameters";
	 *
	 * } else {
	 *
	 * //This is not an adjustment, but an actual packet if(notes.trim().length()==8
	 * && (notes.charAt(0)=='P' || notes.charAt(0)=='L') &&
	 * !notes.equals("PROMOCPN")){
	 *
	 * AddPacketsAfterSales amp = new AddPacketsAfterSales(); message =
	 * amp.addPackets(notes, saleDate, amount, opsId, id); } else {
	 *
	 * AddMiscPackets amp = new AddMiscPackets(); message = amp.addPackets(notes,
	 * saleDate, amount, opsId, id); }
	 *
	 * if(message.equals("")){ success = true; } }
	 *
	 *
	 * return SUCCESS; }
	 */


}
