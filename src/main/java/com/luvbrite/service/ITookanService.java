package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.DeliveredPacketDTO;
import com.luvbrite.model.SoldPacketsDTO;
import com.luvbrite.model.tookan.MetaData_CreateTask;
import com.luvbrite.model.tookan.TookanResponse;

public interface ITookanService {
	public boolean createTaskRequest(int salesID, List<SoldPacketsDTO> sps,int shopId);

	public List<DeliveredPacketDTO> getDeliveryPacketsInfo(List<SoldPacketsDTO> sps, int salesid,int shopId);

	public List<MetaData_CreateTask> getCustomFieldsFromDeliveredPackets(List<DeliveredPacketDTO> dps,int shopId);

	public double getTotalTax(int salesid ,int shopId);

	public double getTotal(List<DeliveredPacketDTO> dps,int shopId);

	public  double getSubtotal(List<DeliveredPacketDTO> dps,int shopId);

	public TookanResponse createTookanResponseObject(String tookanResponse,int shopId);


}
