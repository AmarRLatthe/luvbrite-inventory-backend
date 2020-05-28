package com.luvbrite.model;

public class TaxDetailsDTO {

	private String city;
	private String zipcode;
	private double appliedTax;
	private OrderTaxDTO orderTax;

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public double getAppliedTax() {
		return appliedTax;
	}
	public void setAppliedTax(double appliedTax) {
		this.appliedTax = appliedTax;
	}
	public OrderTaxDTO getOrderTax() {
		return orderTax;
	}
	public void setOrderTax(OrderTaxDTO orderTax) {
		this.orderTax = orderTax;
	}

}
