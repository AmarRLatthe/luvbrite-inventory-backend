package com.luvbrite.model;

public class DispatchSalesInfo {

	private int id = 0;
	//private int clientId = 0;
	private int driverId = 0;
	private int priority = 0;
	private String dateCalled = "";
	private String dateArrived = "";
	private String dateFinished = "";
	private String paymentMode = "";
	private String cancellationReason = "";
	private String clientName = "";
	private String additionalInfo = "";
	private double tip = 0d;
	private double commissionPercent = 0d;
	private String splitAmount = "";
	private double distInMiles = 0d;
	private double lat = 0d;
	private double lng = 0d;
	private String status = ""; // open / closed
	private double totalTaxApplied = 0d;
	private double rushFeeApplied = 0d;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getDateCalled() {
		return dateCalled;
	}
	public void setDateCalled(String dateCalled) {
		this.dateCalled = dateCalled;
	}
	public String getDateArrived() {
		return dateArrived;
	}
	public void setDateArrived(String dateArrived) {
		this.dateArrived = dateArrived;
	}
	public String getDateFinished() {
		return dateFinished;
	}
	public void setDateFinished(String dateFinished) {
		this.dateFinished = dateFinished;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getCancellationReason() {
		return cancellationReason;
	}
	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public double getTip() {
		return tip;
	}
	public void setTip(double tip) {
		this.tip = tip;
	}
	public double getCommissionPercent() {
		return commissionPercent;
	}
	public void setCommissionPercent(double commissionPercent) {
		this.commissionPercent = commissionPercent;
	}
	public String getSplitAmount() {
		return splitAmount;
	}
	public void setSplitAmount(String splitAmount) {
		this.splitAmount = splitAmount;
	}
	public double getDistInMiles() {
		return distInMiles;
	}
	public void setDistInMiles(double distInMiles) {
		this.distInMiles = distInMiles;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getTotalTaxApplied() {
		return totalTaxApplied;
	}
	public void setTotalTaxApplied(double totalTaxApplied) {
		this.totalTaxApplied = totalTaxApplied;
	}
	public double getRushFeeApplied() {
		return rushFeeApplied;
	}
	public void setRushFeeApplied(double rushFeeApplied) {
		this.rushFeeApplied = rushFeeApplied;
	}


}
