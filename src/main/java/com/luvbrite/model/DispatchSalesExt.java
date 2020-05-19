package com.luvbrite.model;

public class DispatchSalesExt extends DispatchSalesInfo{

	private String driverName = "";
	private int dispatchDeliveryRelationId = 0;
	private OnlineOrderInfoDTO ooi = null;
	private String job_status = "";
	private String total_distance = "";

	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public int getDispatchDeliveryRelationId() {
		return dispatchDeliveryRelationId;
	}
	public void setDispatchDeliveryRelationId(int dispatchDeliveryRelationId) {
		this.dispatchDeliveryRelationId = dispatchDeliveryRelationId;
	}
	public OnlineOrderInfoDTO getOoi() {
		return ooi;
	}
	public void setOoi(OnlineOrderInfoDTO ooi) {
		this.ooi = ooi;
	}
	public String getJob_status() {
		return job_status;
	}
	public void setJob_status(String job_status) {
		this.job_status = job_status;
	}
	public String getTotal_distance() {
		return total_distance;
	}
	public void setTotal_distance(String total_distance) {
		this.total_distance = total_distance;
	}

}
