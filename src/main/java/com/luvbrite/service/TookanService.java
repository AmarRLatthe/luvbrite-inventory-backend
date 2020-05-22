package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.DeliveredPacketDTO;
import com.luvbrite.model.SoldPacketsDTO;
import com.luvbrite.model.tookan.MetaData_CreateTask;
import com.luvbrite.model.tookan.TookanResponse;

public class TookanService implements ITookanService  {

	@Override
	public boolean createTaskRequest(int salesID, List<SoldPacketsDTO> sps, int shopId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DeliveredPacketDTO> getDeliveryPacketsInfo(List<SoldPacketsDTO> sps, int salesid, int shopId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MetaData_CreateTask> getCustomFieldsFromDeliveredPackets(List<DeliveredPacketDTO> dps, int shopId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTotalTax(int salesid, int shopId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotal(List<DeliveredPacketDTO> dps, int shopId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSubtotal(List<DeliveredPacketDTO> dps, int shopId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TookanResponse createTookanResponseObject(String tookanResponse, int shopId) {
		// TODO Auto-generated method stub
		return null;
	}







}
