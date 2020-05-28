package com.luvbrite.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.controller.ChangeTrackerDTO;
import com.luvbrite.jdbcutils.DispatchSalesRowMapper;
import com.luvbrite.model.DispatchSalesExt;
import com.luvbrite.model.DispatchUpdateDTO;
import com.luvbrite.model.MiscPacketsDTO;
import com.luvbrite.model.Pagination;
import com.luvbrite.model.PaginationLogic;
import com.luvbrite.model.RoundTripDistanceDTO;
import com.luvbrite.model.SoldPacketsDTO;
import com.luvbrite.repository.IDispatchSalesInfoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DispatchServiceImpl implements IDispatchService {

	@Autowired
	Tracker tracker;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	IDispatchSalesInfoRepository dispatchSalesInfoRepoImpl;

	@Autowired
	MasterInventoryService masterInvService;

	@Autowired
	ITookanService tookanServiceImpl;

	private Pagination pg;
	private final int itemsPerPage = 15;

	@Override
	public List<DispatchSalesExt> listDispatches(Integer driverId, Integer dispatchId, Boolean cancelled,
			Boolean finished, Boolean notFinished, String q, String orderBy, String mode, String qSORTDIR,
			Integer currentPage, Integer deliveryRtId, Integer shopId) throws Exception {

		log.error("Inside list dispatches");

		driverId = (driverId == null) ? 0 : driverId;
		dispatchId = (dispatchId == null) ? 0 : dispatchId;
		cancelled = (cancelled == null) ? false : cancelled;
		finished = (finished == null) ? false : finished;
		notFinished = (notFinished == null) ? false : notFinished;
		q = (q == null) ? "" : q;
		orderBy = (orderBy == null) ? "" : orderBy;
		mode = (mode == null) ? "" : mode;
		qSORTDIR = (qSORTDIR == null) ? "" : qSORTDIR;
		currentPage = (currentPage == null) ? 0 : currentPage;
		deliveryRtId = (deliveryRtId == null) ? 0 : deliveryRtId;
		shopId = (shopId == null) ? 0 : shopId;

		String qWHERE = "", qOFFSET = "", qLIMIT = " LIMIT " + itemsPerPage + " ", qORDERBY = " ORDER by ds.id ";

		qSORTDIR = " ASC";

		int offset = 0;
		PaginationLogic pgl = null;

		if (currentPage <= 0) {
			currentPage = 1;
		}

		if ((orderBy != null) && !orderBy.trim().equals("")) {
			switch (orderBy) {
			case "client":
				qORDERBY = " ORDER BY ds.client_name " + qSORTDIR + ", date_called DESC";
				break;

			case "priority":
				qORDERBY = " ORDER BY ds.priority " + qSORTDIR + ", date_called DESC";
				break;

			case "calltime":
				qORDERBY = " ORDER BY ds.date_called " + qSORTDIR;
				break;

			case "finishtime":
				qORDERBY = " ORDER BY ds.date_finished " + qSORTDIR + ", date_called DESC";
				break;

			default:
				qORDERBY = " ORDER BY ds.id " + qSORTDIR;

			}
		}

		List<DispatchSalesExt> dispatches = new ArrayList<DispatchSalesExt>();

		if (cancelled) {
			qWHERE = " WHERE ds.cancellation_reason <> '' ";

		} else if (finished) {
			qWHERE = " WHERE ds.date_finished IS NOT NULL ";
		} else if (notFinished) {
			qWHERE = " WHERE ds.cancellation_reason = ''  AND ds.date_finished IS NULL ";
		}

		/**
		 * Query String
		 *
		 */
		if ((q != null) && (q.length() > 2)) {
			String qString = " (ds.client_name ~* '.*" + q + ".*' OR ds.id::text ~* '.*" + q
					+ ".*' OR ooi.order_number::text ~* '.*" + q + ".*') ";

			if (qWHERE.equals("")) {
				qWHERE = " WHERE " + qString;
			} else {
				qWHERE += " AND " + qString;
			}
		}

		String driverIdIdFilter = " LEFT JOIN  drivers d ON d.id = ds.driver_id ";
		if (driverId != 0) {
			if (driverId == 999999) {
				driverIdIdFilter = " JOIN  drivers d ON d.id = ds.driver_id AND ds.driver_id != 0 ";
			} else if (driverId == 999998) {
				if (qWHERE.equals("")) {
					qWHERE = " WHERE ds.driver_id = 0 ";
				} else {
					qWHERE += " AND ds.driver_id = 0 ";
				}
			} else {
				driverIdIdFilter = " JOIN  drivers d ON d.id = ds.driver_id AND ds.driver_id = " + driverId + " ";
			}
		}

		String dispatchIdFilter = "", deliveryRouteFilter = "LEFT JOIN disp_delv_rel ddr ON ddr.dispatch_id = ds.id ";
		if (dispatchId != 0) {
			dispatchIdFilter = "WHERE ds.id = " + dispatchId + " ";
		} else if (mode.equals("rd")) {

			qORDERBY = " ORDER by ds.id DESC";

			if (qWHERE.equals("")) {
				qWHERE = " WHERE ddr.id is NULL ";
			} else {
				qWHERE += " AND ddr.id is NULL ";
			}

			if ((q != null) && (q.length() > 2)) {
				String qString = " (ds.client_name ~* '.*" + q + ".*' OR ds.id::text ~* '.*" + q
						+ ".*' OR ooi.order_number::text ~* '.*" + q + ".*') ";
				qWHERE += " AND " + qString;
			}
		} else if (mode.equals("rd-disp")) {
			// int deliveryRtId = routine.getInteger(req.getParameter("dr_id"));
			deliveryRouteFilter = "JOIN disp_delv_rel ddr ON ddr.dispatch_id = ds.id AND ddr.dr_id = " + deliveryRtId
					+ " ";
			qORDERBY = " ORDER by ddr.indx ASC";

		} else {
			String countString = "SELECT COUNT(*) " + "FROM dispatch_sales_info ds "
					+ "LEFT JOIN online_order_info ooi ON ooi.dispatch_sales_id = ds.id "
					+ (driverId != 0 ? driverIdIdFilter : " ") + qWHERE;

			Integer count = jdbcTemplate.queryForObject(countString, Integer.class);

			pgl = new PaginationLogic(count, itemsPerPage, currentPage);

			pg = pgl.getPg();
			offset = pg.getOffset();
			if (offset > 0) {
				qOFFSET = " OFFSET " + offset;
			}

			String tookan_job_details = "LEFT JOIN job_details jobd ON jobd.sales_id = ooi.dispatch_sales_id ";

			StringBuffer queryBuffer = new StringBuffer();

			queryBuffer.append(
					"SELECT jobd.job_status,jobd.tookan_driver_name,jobd.distance_travelled, ds.id as dispatch_sales_id , ds.*, ooi.*, ")
			.append("TO_CHAR(ds.date_called, 'MM/dd/yyyy HH:MI AM') AS formatted_date_called, ")
			.append("TO_CHAR(ds.date_arrived, 'MM/dd/yyyy HH:MI AM') AS formatted_date_arrived, ")
			.append("TO_CHAR(ds.date_finished, 'MM/dd/yyyy HH:MI AM') AS formatted_date_finished, ")
			.append("d.driver_name, ").append("COALESCE(ddr.dr_id,0) AS dr_id ")
			.append("FROM dispatch_sales_info ds ")
			.append("LEFT JOIN online_order_info ooi ON ooi.dispatch_sales_id = ds.id ")
			.append(tookan_job_details).append(deliveryRouteFilter).append(driverIdIdFilter)
			.append(dispatchIdFilter).append(qWHERE).append("AND ooi.shop_id = " + shopId + " ")
			.append(qORDERBY).append(qLIMIT).append(qOFFSET);

			dispatches = jdbcTemplate.query(queryBuffer.toString(), new DispatchSalesRowMapper());

		}
		return dispatches;

	}

	@Override
	public ResponseEntity<CommonResponse> updatePacketInfo(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		String dateCalled = dispatchUpdateDTO.getDateCalled();
		String clientName = dispatchUpdateDTO.getClientName();
		CommonResponse response = new CommonResponse();

		if (clientName.equals("") || ((dateCalled == null) || dateCalled.equals(""))) {

			response.setCode(400);
			response.setData(false);
			response.setMessage("Invalid update params details");
			response.setStatus("FAILED");

			new ResponseEntity<CommonResponse>(response, HttpStatus.OK);

		}

		StringBuffer updatePackteInfo = new StringBuffer();

		updatePackteInfo.append("UPDATE dispatch_sales_info ").append(
				"SET client_name=?, priority = ?, date_called = to_timestamp(?,'MM/dd/yyyy HH:MI AM'), additional_info = ? ")
		.append("WHERE id=?");

		int updateStatus = jdbcTemplate.update(updatePackteInfo.toString(),
				new Object[] { dispatchUpdateDTO.getClientName(), dispatchUpdateDTO.getPriority(),
						dispatchUpdateDTO.getDateCalled(), dispatchUpdateDTO.getAdditionalInfo(),
						dispatchUpdateDTO.getDispatchId() });

		if (updateStatus == 0) {

			log.error("dispatch_sales_info update failed");

			response.setCode(500);
			response.setData(false);
			response.setMessage("dispatch_sales_info update failed");
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
		}

		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("");
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setItemId(dispatchUpdateDTO.getOpsId());

		tracker.track(ct);

		response.setCode(200);
		response.setData(true);
		response.setMessage("dispatch_sales_info updated successfully");
		response.setStatus("SUCCESS");

		return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<CommonResponse> assignDriver(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> cancelDispatch(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {

		String reason = dispatchUpdateDTO.getReason();

		CommonResponse response = new CommonResponse();

		String message = "";

		int dispatchId = dispatchUpdateDTO.getDispatchId();

		if (reason == null) {
			message = "Invalid reason";

			response.setCode(200);
			response.setData(false);
			response.setMessage(message);
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
		}

		if (dispatchId == 0) {
			message = "dispatchId can not be 0";

			response.setCode(400);
			response.setData(false);
			response.setMessage(message);
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
		}

		StringBuffer updateOrderCancelReason = new StringBuffer();
		updateOrderCancelReason.append("UPDATE dispatch_sales_info ").append("SET").append("cancellation_reason=?")
		.append("WHERE id=?");

		int updateStatus = jdbcTemplate.update(updateOrderCancelReason.toString(),
				new Object[] { dispatchUpdateDTO.getReason(), dispatchUpdateDTO.getDispatchId() });

		if (updateStatus == 0) {
			message = "dispatch_sales_info - cancellation_reason update failed";
			log.error("dispatch_sales_info - cancellation_reason update failed. Q - ");

			response.setCode(500);
			response.setData(false);
			response.setMessage(message);
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);

		}

		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("cancellation_reason updated to " + reason);
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setItemId(dispatchUpdateDTO.getDispatchId());
		ct.setOperatorId(dispatchUpdateDTO.getOpsId());

		tracker.track(ct);

		response.setCode(200);
		response.setData(true);
		response.setMessage(message);
		response.setStatus("SUCCESS");

		return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<CommonResponse> markArrived(DispatchUpdateDTO dispatchUpdateDTO,int shopId) throws Exception {

		CommonResponse response = new CommonResponse();

		String datetime = dispatchUpdateDTO.getDatetime();

		String message = "";
		if (datetime == null) {
			message = "Invalid arrival time";

			response.setCode(400);
			response.setData(false);
			response.setMessage(message);
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
		}

		StringBuffer markedArrived = new StringBuffer();

		markedArrived.append("UPDATE dispatch_sales_info ").append("SET ")
		.append("date_arrived=to_timestamp(?,'MM/dd/yyyy HH:MI AM')").append("WHERE id=?");

		int updateStatus = jdbcTemplate.update(markedArrived.toString(),
				new Object[] { datetime, dispatchUpdateDTO.getDispatchId() });

		if (updateStatus == 0) {
			message = "dispatch_sales_info - date_arrived update failed";

			log.error("dispatch_sales_info - date_arrived update failed. Q - ");

			response.setCode(500);
			response.setData(false);
			response.setMessage(message);
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
		}

		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("date_arrived updated to " + datetime);
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setItemId(dispatchUpdateDTO.getDispatchId());
		ct.setOperatorId(dispatchUpdateDTO.getOpsId());
		ct.setShopId(shopId);

		tracker.track(ct);

		return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<CommonResponse> markSold(DispatchUpdateDTO dispatchUpdateDTO, int operatorId,int shopId)
			throws Exception {

		CommonResponse response = new CommonResponse();

		String datetime = dispatchUpdateDTO.getDatetime();
		String soldPackets = dispatchUpdateDTO.getSoldPackets();
		String paymentMode = dispatchUpdateDTO.getPaymentMode();
		String split = dispatchUpdateDTO.getSplit() == null ? "" : dispatchUpdateDTO.getSplit();

		double discount = dispatchUpdateDTO.getDiscount();

		if ((datetime == null) || ((soldPackets == null) || soldPackets.equals(""))
				|| ((paymentMode == null) || paymentMode.equals(""))) {

			response.setData(null);
			response.setStatus("FAILED");
			response.setCode(400);
			response.setMessage("Invalid update parameters");
			new ResponseEntity<>(response, HttpStatus.OK);
		}

		String discountString = "";
		if (discount != 0) {
			discountString = ", additional_info = additional_info || '**Discount provided:" + discount + "%**' ";
		}

		// Find distance travelled for this sales and the latitude and longitude for the
		// location
		RoundTripDistanceDTO roundTripDistance = dispatchSalesInfoRepoImpl
				.getLocationOfClient(dispatchUpdateDTO.getDispatchId(),shopId);

		boolean updateStatus = dispatchSalesInfoRepoImpl.updateDispatchSalesInfo(roundTripDistance, dispatchUpdateDTO,
				discountString,shopId);

		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("Marked Sold");
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setItemId(dispatchUpdateDTO.getDispatchId());
		ct.setOperatorId(operatorId);

		tracker.track(ct);

		ArrayList<Integer> packetIds = new ArrayList<Integer>();
		SoldPacketsDTO[] spArray = new Gson().fromJson(soldPackets, SoldPacketsDTO[].class);
		List<SoldPacketsDTO> sps = Arrays.asList(spArray);

		if ((sps != null) && (sps.size() > 0)) {
			/**
			 * Update each packets and mark then as sold! *
			 */
			List<Integer> salesIdlist = new ArrayList<Integer>();

			for (SoldPacketsDTO sp : sps) {

				salesIdlist.add(sp.getId());

				if (dispatchSalesInfoRepoImpl.updatePacketsAsSold(sp, dispatchUpdateDTO, operatorId,shopId)) {
					ct = new ChangeTrackerDTO();
					ct.setActionDetails("date_sold, sales_id, selling_price  updated to " + datetime + ", " + operatorId + ", "
							+ sp.getSellingPrice());
					ct.setActionType("update");
					ct.setActionOn("packet");
					ct.setItemId(sp.getId());
					ct.setOperatorId(operatorId);
					ct.setShopId(shopId);
					packetIds.add(sp.getId());
					tracker.track(ct);

				}
			}

			for (int i = 0; i < salesIdlist.size(); i++) { // Updating products_available table, when some product is
				// markSold
				masterInvService.updateProductsAvailable(salesIdlist.get(i), shopId);

			}

			/*
			 * if (packetIds.size() > 0) { InvAlertOps ivo = new InvAlertOps();
			 * ivo.CheckInventoryStatus(ncon, packetIds);
			 *
			 * ivo = null; }
			 */

		}

		String misPackets = "";
		try {
			misPackets = dispatchUpdateDTO.getMis();
			if ((misPackets != null) && (misPackets.length() > 3)) {
				MiscPacketsDTO[] mpArray = new Gson().fromJson(misPackets, MiscPacketsDTO[].class);
				List<MiscPacketsDTO> mps = Arrays.asList(mpArray);
				if (mps.size() > 0) {

					AddMiscPackets amp = new AddMiscPackets();
					for (MiscPacketsDTO mp : mps) {
						amp.addPackets(mp.getItemDesc(), datetime, mp.getItemPrice(), operatorId, dispatchUpdateDTO.getDispatchId(),shopId);
					}

					amp = null;
				}
			}
		} catch (Exception e) {
			log.error("Error working with Misc Packets. Sales ID:" + dispatchUpdateDTO.getId() + ", opsId:" + operatorId + ", misPackets:"
					+ misPackets + ". " + e.getMessage());
		}

		/**
		 * This will create Tookan task and save job details *
		 */

		tookanServiceImpl.createTaskRequestOnTookan(dispatchUpdateDTO.getDispatchId(), sps, shopId, operatorId);

		response.setCode(200);
		response.setStatus("SUCCESS");
		response.setMessage("Packet sold successfully");
		response.setData(true);

		return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);


	}

	@Override
	public ResponseEntity<CommonResponse> inOfficeOrderProcess(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> dateUpdate(DispatchUpdateDTO dispatchUpdateDTO,int shopId ,int operatorId) throws Exception {


		CommonResponse response =  new CommonResponse();

		String datetime = dispatchUpdateDTO.getDateArrived();

		if (datetime == null) {

			response.setMessage("Invalid date");
			response.setCode(400);
			response.setStatus("FAILED");
			response.setData(false);

			new ResponseEntity<CommonResponse>(response , HttpStatus.ACCEPTED);

		}


		if(!dispatchSalesInfoRepoImpl.updateDate(dispatchUpdateDTO, shopId)) {

			response.setCode(500);
			response.setData(false);
			response.setMessage("Date arrived update failed");
			response.setStatus("FAILED");

			log.error("date arrived failed");
			return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);

		}


		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("date_finished updated to " + datetime);
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setShopId(shopId);
		ct.setItemId(dispatchUpdateDTO.getDispatchId());
		ct.setOperatorId(operatorId);

		tracker.track(ct);

		if(!dispatchSalesInfoRepoImpl.updatePacketSoldDate(dispatchUpdateDTO.getDatetime(), shopId)) {

			response.setCode(500);
			response.setData(false);
			response.setMessage("Packet sold date update failed");
			response.setStatus("FAILED");

			log.error("packet date sold update failed ");
			return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
		}



		ct = new ChangeTrackerDTO();
		ct.setActionDetails("date_sold on corresponding packets updated to " + datetime);
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setItemId(dispatchUpdateDTO.getDispatchId());
		ct.setOperatorId(operatorId);

		tracker.track(ct);

		response.setCode(200);
		response.setData(false);
		response.setMessage("date updated successfully");
		response.setStatus("FAILED");

		log.error("packet date sold update failed ");
		return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);

	}

	@Override
	public ResponseEntity<CommonResponse> pmtModeUpdate(DispatchUpdateDTO dispatchUpdateDTO,int shopId,int operatorId) throws Exception {

		CommonResponse response = new CommonResponse();
		String pmtMode = dispatchUpdateDTO.getPaymentMode();

		if (pmtMode == null) {
			response.setCode(400);
			response.setData(false);
			response.setMessage("Invalid payment mode");
			response.setStatus("FAILED");;

			return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
		}

		if(!dispatchSalesInfoRepoImpl.updatePaymentMode(dispatchUpdateDTO.getDispatchId(), pmtMode, shopId)) {
			response.setCode(500);
			response.setData(false);
			response.setMessage("dispatch_sales_info - pmtMode update failed");
			response.setStatus("FAILED");;

			return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
		}
		//System.out.println("Update Dispatch - dispatch_sales_info update. Q - " + pst);


		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("payment mode updated to " + pmtMode);
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setItemId(dispatchUpdateDTO.getDispatchId());
		ct.setOperatorId(operatorId);

		tracker.track(ct);


		response.setCode(200);
		response.setData(true);
		response.setMessage("payment mode updated to "+pmtMode);
		response.setStatus("SUCCESS");;

		return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);

	}






	@Override
	public ResponseEntity<CommonResponse> tipUpdate(DispatchUpdateDTO dispatchUpdateDTO,int shopId,int operatorId) throws Exception {

		CommonResponse response  =  new CommonResponse();

		if(!dispatchSalesInfoRepoImpl.updateTip(dispatchUpdateDTO.getDispatchId(), dispatchUpdateDTO.getTip(), shopId)) {

			response.setCode(500);
			response.setData(false);
			response.setMessage("Tip update failed !!");
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
		}


		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("tip updated to " + dispatchUpdateDTO.getTip());
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setItemId(dispatchUpdateDTO.getDispatchId());
		ct.setOperatorId(operatorId);

		tracker.track(ct);

		response.setCode(200);
		response.setData(false);
		response.setMessage("Tip update success !!");
		response.setStatus("SUCCESS");

		return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<CommonResponse> splitUpdate(DispatchUpdateDTO dispatchUpdateDTO,int shopId,int opsId) throws Exception {

		String spliAmt = dispatchUpdateDTO.getSplitAmt() == null ? "":dispatchUpdateDTO.getSplitAmt();

		CommonResponse response =  new CommonResponse();

		if(!dispatchSalesInfoRepoImpl.updateSplitAmt(dispatchUpdateDTO.getDispatchId(), shopId)) {
			response.setCode(500);
			response.setStatus("FAILED");
			response.setMessage("dispatch_sales_info - splitAmt update failed");
			response.setData(false);

			return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
		}


		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("splitAmt updated to " + spliAmt);
		ct.setActionType("update");
		ct.setActionOn("dispatch");
		ct.setItemId(dispatchUpdateDTO.getDispatchId());
		ct.setOperatorId(opsId);

		tracker.track(ct);

		response.setCode(200);
		response.setStatus("SUCCESS");
		response.setMessage("dispatch_sales_info - splitAmt update successfully");
		response.setData(false);

		return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<CommonResponse> recalculateDistance(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> closeTheseSales(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {

		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> reopenTheseSales(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> resetSale(DispatchUpdateDTO dispatchUpdateDTO,int shopId,int operatorId) throws Exception {

		CommonResponse response =  new CommonResponse();

		if(!dispatchSalesInfoRepoImpl.resetDateFinished(dispatchUpdateDTO.getDispatchId(), shopId)) {

			response.setCode(500);
			response.setData(false);
			response.setData("Sales could not be reset");
			response.setStatus("FAILED");

			return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
		}
		//Getting list of different productIds for particular sales


		List<Integer> productIdsForSales = 	masterInvService.getListOfProductIdsForSales(dispatchUpdateDTO.getDispatchId() ,shopId);

		int noOfPacketsReset = 	dispatchSalesInfoRepoImpl.resetPacketUpdate(dispatchUpdateDTO.getDispatchId(), shopId);


		for(Integer productId :productIdsForSales) {
			masterInvService.updateProducts(productId, shopId);

		}

		tookanServiceImpl.cancelJobCreated(dispatchUpdateDTO.getDispatchId(), shopId, operatorId);

		response.setCode(200);
		response.setData(true);
		response.setData("");
		response.setStatus(noOfPacketsReset+" are removed from sales successfully !!!");

		return new ResponseEntity<CommonResponse>(response,HttpStatus.ACCEPTED);
	}

}


