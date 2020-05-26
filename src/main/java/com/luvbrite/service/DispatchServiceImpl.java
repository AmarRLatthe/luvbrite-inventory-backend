package com.luvbrite.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.controller.ChangeTrackerDTO;
import com.luvbrite.jdbcutils.DispatchSalesRowMapper;
import com.luvbrite.model.DispatchSalesExt;
import com.luvbrite.model.DispatchUpdateDTO;
import com.luvbrite.model.Pagination;
import com.luvbrite.model.PaginationLogic;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DispatchServiceImpl implements IDispatchService {

	@Autowired
	Tracker tracker;

	@Autowired
	JdbcTemplate jdbcTemplate;

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
	public ResponseEntity<CommonResponse> markArrived(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {

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

		tracker.track(ct);

		return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<CommonResponse> markSold(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> inOfficeOrderProcess(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> dateUpdate(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> pmtModeUpdate(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> tipUpdate(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> splitUpdate(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> recalculateDistance(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> closeTheseSales(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> reopenTheseSales(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<CommonResponse> resetSale(DispatchUpdateDTO dispatchUpdateDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
