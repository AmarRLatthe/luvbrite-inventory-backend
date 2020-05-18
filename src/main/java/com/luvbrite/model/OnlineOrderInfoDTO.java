package com.luvbrite.model;


public class OnlineOrderInfoDTO {
	private int id = 0;
	private int dispacthSalesId = 0;
	private double total = 0d;
	private double discount = 0d;
	private String orderNumber = "";
	private String address = "";
	private TaxDetailsDTO taxDetails;
	private String phone = "";
	private String note = "";
	private String deliveryNote = "";
	private double rushFee = 0d;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDispacthSalesId() {
		return dispacthSalesId;
	}
	public void setDispacthSalesId(int dispacthSalesId) {
		this.dispacthSalesId = dispacthSalesId;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDeliveryNote() {
		return deliveryNote;
	}
	public void setDeliveryNote(String deliveryNote) {
		this.deliveryNote = deliveryNote;
	}

	public TaxDetailsDTO getTaxDetails() {
		return taxDetails;
	}
	public void setTaxDetails(TaxDetailsDTO taxDetails) {
		this.taxDetails = taxDetails;
	}

	public double getRushFee() {
		return rushFee;
	}
	public void setRushFee(double rushFee) {
		this.rushFee = rushFee;
	}

}
