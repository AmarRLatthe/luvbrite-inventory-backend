
package com.luvbrite.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luvbrite.commons.ListOfSoldPacketCode;
import com.luvbrite.constants.APIs;
import com.luvbrite.controller.ChangeTrackerDTO;
import com.luvbrite.jdbcutils.model.JobInsertDetail;
import com.luvbrite.model.DeliveredPacketDTO;
import com.luvbrite.model.SoldPacketsDTO;
import com.luvbrite.model.orderdata.OrderMain;
import com.luvbrite.model.tookan.CancelJobCreatedRequest;
import com.luvbrite.model.tookan.MetaData_CreateTask;
import com.luvbrite.model.tookan.TaskRequest;
import com.luvbrite.model.tookan.TookanResponse;
import com.luvbrite.model.tookan.TrackingLinkEmailInfo;
import com.luvbrite.repository.ITookanTaskRepository;
import com.struts.core.CreateTookanTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TookanServiceImpl implements ITookanService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	Tracker track;

	@Autowired
	ITookanTaskRepository tookanTaskRepoImpl;

	@Autowired
	RestTemplate restTemplate;

	private static final String TOOKAN_API_KEY = APIs.TOOKAN_API_KEY;

	private static final String TOOKAN_CREATE_TASK_URL = APIs.TOOKAN_CREATE_TASK;

	private static DecimalFormat decimalFormat = new DecimalFormat("#.##");



	@Override
	public boolean createTaskRequestOnTookan(int salesId, List<SoldPacketsDTO> sps, int shopId,int operatorId) {

		if (salesId == 0) {
			log.info("Invalid salesID i.e 0 ");
			return false;
		}
		if (sps.isEmpty()) {
			log.info("there are no sold packets");
			return false;
		}
		List<DeliveredPacketDTO> dps = getDeliveryPacketsInfo(sps, salesId, shopId);



		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		OrderMain order = null;
		TaskRequest createTaskRequest = null;
		String orderNumber = "";
		String customerName = "";
		String response = "";
		boolean isTaskCreated = false;

		String[] ref_images = { "https://lbrit.co/static/imgs/luvbrite-header-logo-s.png",
		"https://lbrit.co/static/imgs/luvbrite-header-logo-s.png" };

		TaskRequest tooakanRequest =	tookanTaskRepoImpl.createTaskRequest(salesId, shopId);


		tooakanRequest.setApi_key(APIs.TOOKAN_API_KEY);
		tooakanRequest.setRef_images(ref_images);
		tooakanRequest.setMeta_data(getCustomFieldsFromDeliveredPackets(dps, shopId));


		TookanResponse tookanResponse =	restTemplate.getForObject(TOOKAN_CREATE_TASK_URL, TookanResponse.class, tooakanRequest);


		saveJobDetails(tookanResponse, salesId, shopId, operatorId);


		//TookanResponse tookanResponse = restTemplate.getForObject(APIs.TOOKAN_CREATE_TASK, TookanResponse.class);
		try {
			log.info("Create Task Query:------" + taskDetailsQuery);
			con = DBPoolAccess.sDBPoolAccess.getConnection();
			pst = con.prepareStatement(taskDetailsQuery);
			rs = pst.executeQuery(); // String[]
			if (rs.next()) {
				createTaskRequest = new TaskRequest();
				createTaskRequest.setApi_key(APIs.TOOKAN_API_KEY);
				orderNumber = rs.getString("order_number");
				createTaskRequest.setOrder_id(orderNumber);
				createTaskRequest.setJob_description("Luvbrite Order Delivery");
				String customer_email = rs.getString("email");
				createTaskRequest.setCustomer_email(customer_email);
				customerName = rs.getString("client_name");
				createTaskRequest.setCustomer_username(customerName);
				createTaskRequest.setCustomer_phone(rs.getString("phone"));
				createTaskRequest.setCustomer_address(rs.getString("address"));
				createTaskRequest.setLatitude(rs.getString("lat"));
				createTaskRequest.setLongitude(rs.getString("lng"));
				createTaskRequest.setJob_description("delivering cannabis");
				createTaskRequest.setJob_delivery_datetime(rs.getString("date_finished"));
				createTaskRequest.setCustom_field_template("Cannabis_Delivery");
				createTaskRequest.setMeta_data(getCustomFieldsFromDeliveredPackets(dps, shopId));
				createTaskRequest.setTeam_id("");
				createTaskRequest.setAuto_assignment("0");
				createTaskRequest.setHas_pickup("0");
				createTaskRequest.setHas_delivery("1");
				createTaskRequest.setLayout_type("0");
				createTaskRequest.setTracking_link(1);
				createTaskRequest.setTimezone("+480");
				createTaskRequest.setFleet_id("");
				tooakanRequest.setRef_images(ref_images);
				createTaskRequest.setNotify(1);
				createTaskRequest.setTags("");
				createTaskRequest.setGeofence(0);


				response = postCreateTaskRequest(createTaskRequest);

				log.info("Tookan Response $$$$$$$$ " + response);
				if (new SaveJobDetails(response, salesID).saveJobDetails()) {
					log.info("Successfully saved created job details to db of sales_id" + salesID);

					TookanResponse tookanResponse = createTookanResponseObject(response);

					if (tookanResponse.getStatus().equals("200")) {

						isTaskCreated = true;

						TrackingLinkEmailInfo trackLnkEmailInfoObj = new TrackingLinkEmailInfo();
						trackLnkEmailInfoObj.setOrderDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
						trackLnkEmailInfoObj.setOrderNumber(orderNumber);
						trackLnkEmailInfoObj.setOrderTotal(getTotal(dps) + "");
						trackLnkEmailInfoObj.setTrackingLink(tookanResponse.getData().getTracking_link());
						trackLnkEmailInfoObj.setRecipentEmail(customer_email);
						trackLnkEmailInfoObj.setRecipentName(customerName);
						new TrackingLinkToCustomer().send(trackLnkEmailInfoObj, customer_email);

					}

				} else {
					log.info("Could not save job details for sales id " + salesID);
					isTaskCreated = false;
				}
			} else {
				log.debug("Did not find any details for orderID:-" + orderNumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while creating delivery Task ");
			isTaskCreated = false;
		} finally {
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (Exception e) {

				}
			}

			if (con != null) {
				try {
					con.close();
					con = null;
				} catch (Exception e) {

				}
			}
		}

		return isTaskCreated;
	}






	@Override
	public boolean saveJobDetails(TookanResponse taskResponse, int salesId, int shopId, int operatorId)
			throws Exception {

		JobInsertDetail jobInsertDetail = null;



		if (isJobCreatedSuccessfully(taskResponse, salesId, shopId)) {
			log.info("Task is  created successfully for salesID : " + salesId);
			tookanTaskRepoImpl.deleteAlreadyCreatedTask(salesId, shopId);
			jobInsertDetail = tookanTaskRepoImpl.saveJobDetails(taskResponse, salesId,shopId);

		}

		if (jobInsertDetail == null) {
			log.error("task could not be saved for shop {} , sales {} , operator{} " + shopId + " " + " " + salesId
					+ " " + operatorId);

			return false;
		}

		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("Task Created");
		ct.setActionType("insert");
		ct.setActionOn("Tookan");
		ct.setOperatorId(operatorId);
		ct.setItemId(jobInsertDetail.getJobId()); //
		ct.setDate(jobInsertDetail.getCreatedAt()); // Set Current Date new UpdateTracker(ct, con);

		track.track(ct);

		return true;

	}


	@Override
	public boolean isJobCreatedSuccessfully(TookanResponse createTaskResponse, int salesId, int shopId) {
		boolean isJobCreationSuccessful = false;

		if (!createTaskResponse.getStatus().equals("200")) {
			log.error("Task could not be created because :- " + createTaskResponse.getMessage() + " for salesID :- "
					+ salesId + "for shop :" + shopId);
			isJobCreationSuccessful = false;

		} else {
			isJobCreationSuccessful = true;
		}

		return isJobCreationSuccessful;
	}









	public boolean cancelJobCreated(int salesId, int shopId, int operatorId) {

		boolean isJobCancelled = false;
		if (salesId == 0) {
			log.info("0 is not a valid salesID");
			return false;
		}

		Long jobId = 0l;

		jobId = tookanTaskRepoImpl.isJobAlreadyExists(salesId, shopId);

		if (jobId > 0) {
			TookanResponse tookanResponse = postCancelJobRequest(jobId, restTemplate);
			isJobCancelled = tookanTaskRepoImpl.updateJobCancelStatus(tookanResponse, jobId, shopId);

			return isJobCancelled;
		}

		return isJobCancelled;

	}




	@Override
	public TookanResponse postCancelJobRequest(long jobId, RestTemplate restTemplate) {
		String cancelJoburl = APIs.TOOKAN_CANCEL_TASK;

		CancelJobCreatedRequest cancelJob = new CancelJobCreatedRequest();
		cancelJob.setApi_key(TOOKAN_API_KEY);
		cancelJob.setJob_id(String.valueOf(jobId));
		String cancelJobResponse = "";
		TookanResponse tookanResponse = null;

		try {

			ObjectMapper mapper = new ObjectMapper();
			String cancelJobRequest = mapper.writeValueAsString(cancelJob);
			log.info("CancelJobRequest:-" + cancelJobRequest);

			tookanResponse = restTemplate.postForObject(cancelJoburl, cancelJobRequest, TookanResponse.class);

			cancelJobResponse = mapper.writeValueAsString(tookanResponse);
			log.info("job cancel response from :-" + cancelJobResponse);

		} catch (JsonProcessingException ex) {
			log.error("JsonProcessException while processing cancelJobRequest object", ex);
			return null;
		} catch (Exception ex) {
			log.error("Exception occured while posting cancel job response", ex);
			return null;
		}
		return tookanResponse;
	}



	@Override
	public List<DeliveredPacketDTO> getDeliveryPacketsInfo(List<SoldPacketsDTO> sps, int salesid, int shopId) {
		return tookanTaskRepoImpl.deliveredPackets(sps, salesid, shopId);
	}

	@Override
	public List<MetaData_CreateTask> getCustomFieldsFromDeliveredPackets(List<DeliveredPacketDTO> dps, int shopId) {
		MetaData_CreateTask metadata = null;
		List<MetaData_CreateTask> metadata_customFields = new ArrayList<MetaData_CreateTask>();

		metadata = new MetaData_CreateTask();
		metadata.setLabel("Products");
		metadata.setData(
				ListOfSoldPacketCode.getCommaSeparatedValues(ListOfSoldPacketCode.retrieveProductNames(dps), true));
		metadata_customFields.add(metadata);

		metadata = new MetaData_CreateTask();
		metadata.setLabel("Quantity");
		metadata.setData(ListOfSoldPacketCode
				.getItemNumberCommaSeparated(ListOfSoldPacketCode.retrieveQuantityForEachProduct(dps)));
		metadata_customFields.add(metadata);

		metadata = new MetaData_CreateTask();
		metadata.setLabel("SubTotal");
		metadata.setData(decimalFormat.format(getSubtotal(dps)) + "$");
		metadata_customFields.add(metadata);

		metadata = new MetaData_CreateTask();
		metadata.setLabel("TotalTax");
		metadata.setData(decimalFormat.format(getTotalTax(CreateTookanTask.salesID)) + " $");
		metadata_customFields.add(metadata);

		metadata = new MetaData_CreateTask();
		metadata.setLabel("Total");
		metadata.setData(decimalFormat.format(getTotal(dps)) + " $");
		metadata_customFields.add(metadata);

		return metadata_customFields;
	}

	@Override
	public double getTotalTax(int salesid, int shopId) {
		return 0;
	}

	@Override
	public double getTotal(List<DeliveredPacketDTO> dps, int shopId) {
		return 0;
	}

	@Override
	public double getSubtotal(List<DeliveredPacketDTO> dps, int shopId) {
		return 0;
	}

	@Override
	public TookanResponse createTookanResponseObject(String tookanResponse, int shopId) {
		return null;
	}
}
