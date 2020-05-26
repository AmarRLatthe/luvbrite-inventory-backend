package com.luvbrite.service;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.luvbrite.model.DeliveredPacketDTO;
import com.luvbrite.model.SoldPacketsDTO;
import com.luvbrite.model.tookan.MetaData_CreateTask;
import com.luvbrite.model.tookan.TookanResponse;

public interface ITookanService {
	public boolean createTaskRequestOnTookan(int salesID, List<SoldPacketsDTO> sps,int shopId,int operatorId);

	public List<DeliveredPacketDTO> getDeliveryPacketsInfo(List<SoldPacketsDTO> sps, int salesid,int shopId);

	public List<MetaData_CreateTask> getCustomFieldsFromDeliveredPackets(List<DeliveredPacketDTO> dps,int shopId);

	public double getTotalTax(int salesid ,int shopId);

	public double getTotal(List<DeliveredPacketDTO> dps,int shopId);

	public  double getSubtotal(List<DeliveredPacketDTO> dps,int shopId);

	public TookanResponse createTookanResponseObject(String tookanResponse,int shopId);

	public boolean isJobCreatedSuccessfully(TookanResponse createTaskResponse, int salesId, int shopId);

	public boolean saveJobDetails(TookanResponse taskResponse, int salesId, int shopId, int operatorId) throws Exception;

	public TookanResponse postCancelJobRequest(long jobId, RestTemplate restTemplate);


}
