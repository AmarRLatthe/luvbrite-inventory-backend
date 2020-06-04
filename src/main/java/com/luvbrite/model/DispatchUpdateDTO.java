package com.luvbrite.model;

public class DispatchUpdateDTO {


	String dateArrived;
	private String mode;
	private String mis;
	private String clientName;
	private String dateCalled;
	private String additionalInfo;
	private String reason;
	private String datetime;
	private String soldPackets;
	private String paymentMode;
	private String split;
	private String splitAmt;
	private String pmtMode;
	private String saleIds;
	private int priority;
	private int driverId;
	private int dispatchId;
	private int opsId;
	private double discount;
	private double tip;
	private double rushFeeApplied;






	public int getOpsId() {
		return opsId;
	}

	public void setOpsId(int opsId) {
		this.opsId = opsId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	private double taxApplied;
	private int shopId;

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getDateCalled() {
		return dateCalled;
	}

	public void setDateCalled(String dateCalled) {
		this.dateCalled = dateCalled;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getSoldPackets() {
		return soldPackets;
	}

	public void setSoldPackets(String soldPackets) {
		this.soldPackets = soldPackets;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public String getSplitAmt() {
		return splitAmt;
	}

	public void setSplitAmt(String splitAmt) {
		this.splitAmt = splitAmt;
	}

	public String getPmtMode() {
		return pmtMode;
	}

	public void setPmtMode(String pmtMode) {
		this.pmtMode = pmtMode;
	}

	public String getSaleIds() {
		return saleIds;
	}

	public void setSaleIds(String saleIds) {
		this.saleIds = saleIds;
	}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getTip() {
		return tip;
	}

	public void setTip(double tip) {
		this.tip = tip;
	}

	public double getRushFeeApplied() {
		return rushFeeApplied;
	}

	public void setRushFeeApplied(double rushFeeApplied) {
		this.rushFeeApplied = rushFeeApplied;
	}

	public double getTaxApplied() {
		return taxApplied;
	}

	public void setTaxApplied(double taxApplied) {
		this.taxApplied = taxApplied;
	}

	private String id;
	private String cancelled;
	private String opsid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getCancelled() {
		return cancelled;
	}

	public void setCancelled(String cancelled) {
		this.cancelled = cancelled;
	}

	public String getOpsid() {
		return opsid;
	}

	public void setOpsid(String opsid) {
		this.opsid = opsid;
	}

	public String getMode() {
		this.mode = this.mode == null ? "basic" : this.mode;
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getDispatchId() {
		return dispatchId;
	}

	public void setDispatchId(int dispatchId) {
		this.dispatchId = dispatchId;
	}


	public String getDateArrived() {
		return dateArrived;
	}

	public void setDateArrived(String dateArrived) {
		this.dateArrived = dateArrived;
	}
	public String getMis() {
		return mis;
	}

	public void setMis(String mis) {
		this.mis = mis;
	}


}
