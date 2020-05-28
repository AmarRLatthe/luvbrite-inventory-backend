
package com.luvbrite.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.luvbrite.commons.ListOfSoldPacketCode;
import com.luvbrite.constants.APIs;
import com.luvbrite.model.DeliveredPacketDTO;
import com.luvbrite.model.SoldPacketsDTO;
import com.luvbrite.model.tookan.MetaData_CreateTask;
import com.luvbrite.model.tookan.TaskRequest;
import com.luvbrite.model.tookan.TookanResponse;
import com.luvbrite.repository.ITookanTaskRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TookanServiceImpl implements ITookanService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	Tracker tracker;

	@Autowired
	ITookanTaskRepository tookanTaskRepoImpl;

	@Autowired
	ITookanTaskCancelService tookanTaskCancelServiceImpl;

	@Autowired
	ISaveJobDetailsService saveJobDetailsServiceImpl;

	@Autowired
	RestTemplate restTemplate;



	private static final String TOOKAN_API_KEY = APIs.TOOKAN_API_KEY;

	private static final String TOOKAN_CREATE_TASK_URL = APIs.TOOKAN_CREATE_TASK;

	private static DecimalFormat decimalFormat = new DecimalFormat("#.##");



	@Override
	public boolean createTaskRequestOnTookan(int salesId, List<SoldPacketsDTO> sps, int shopId,int operatorId) throws Exception {

		if (salesId == 0) {
			log.info("Invalid salesID i.e 0 ");
			return false;
		}
		if (sps.isEmpty()) {
			log.info("there are no sold packets");
			return false;
		}
		List<DeliveredPacketDTO> dps = getDeliveryPacketsInfo(sps, salesId, shopId);

		boolean isTaskCreated = false;

		String[] ref_images = { "https://lbrit.co/static/imgs/luvbrite-header-logo-s.png",
		"https://lbrit.co/static/imgs/luvbrite-header-logo-s.png" };

		TaskRequest tooakanRequest =	tookanTaskRepoImpl.createTaskRequest(salesId, shopId);

		//Adding Api key  required parameters to create task request
		tooakanRequest.setApi_key(TOOKAN_API_KEY);
		tooakanRequest.setRef_images(ref_images);
		tooakanRequest.setMeta_data(getCustomFieldsFromDeliveredPackets(dps, salesId,shopId));


		TookanResponse tookanResponse =	restTemplate.getForObject(TOOKAN_CREATE_TASK_URL, TookanResponse.class, tooakanRequest);

		isTaskCreated =	saveJobDetails(tookanResponse, salesId, shopId, operatorId);

		return isTaskCreated;
	}


	@Override
	public boolean saveJobDetails(TookanResponse taskResponse, int salesId, int shopId, int operatorId)
			throws Exception {


		return	saveJobDetailsServiceImpl.saveJobDetails(taskResponse, salesId, shopId, operatorId);


	}


	@Override
	public boolean isJobCreatedSuccessfully(TookanResponse createTaskResponse, int salesId, int shopId) {
		return false;

	}


	public boolean cancelJobCreated(int salesId, int shopId, int operatorId) {

		return	tookanTaskCancelServiceImpl.cancelJobCreated(salesId, shopId, operatorId);

	}


	@Override
	public TookanResponse postCancelJobRequest(long jobId, RestTemplate restTemplate) {
		return tookanTaskCancelServiceImpl.postCancelJobRequest(jobId, restTemplate);
	}



	@Override
	public List<DeliveredPacketDTO> getDeliveryPacketsInfo(List<SoldPacketsDTO> sps, int salesId, int shopId) {
		return tookanTaskRepoImpl.deliveredPackets(sps, salesId, shopId);
	}

	@Override
	public List<MetaData_CreateTask> getCustomFieldsFromDeliveredPackets(List<DeliveredPacketDTO> dps,int salesId, int shopId) {
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
		metadata.setData(decimalFormat.format(getTotalTax(salesId,shopId)) + " $");
		metadata_customFields.add(metadata);

		metadata = new MetaData_CreateTask();
		metadata.setLabel("Total");
		metadata.setData(decimalFormat.format(getTotal(dps,salesId, shopId)) + " $");
		metadata_customFields.add(metadata);

		return metadata_customFields;
	}

	@Override
	public double getTotalTax(int salesid, int shopId) {
		return tookanTaskRepoImpl.getTotalTax(salesid,shopId);
	}

	@Override
	public double getTotal(List<DeliveredPacketDTO> dps, int salesId,int shopId) {
		double total = 0.00f;
		for (DeliveredPacketDTO dp : dps) {
			total = dp.getTotal() + total;
		}
		total = total + getTotalTax(salesId ,shopId);
		return total;
	}

	@Override
	public double getSubtotal(List<DeliveredPacketDTO> dps) {
		if (dps == null) {
			log.debug("Please DeliveredPackets Info cannnot be null");
			return 0.00d;
		}
		double subTotal = 0.0d;
		for (DeliveredPacketDTO dp : dps) {
			subTotal = dp.getTotal() + subTotal;
		}
		return subTotal;
	}

	@Override
	public TookanResponse createTookanResponseObject(String tookanResponse, int shopId) {
		return null;
	}
}
