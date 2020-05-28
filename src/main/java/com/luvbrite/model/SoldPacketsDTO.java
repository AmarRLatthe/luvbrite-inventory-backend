package com.luvbrite.model;

public class SoldPacketsDTO {
	private int id = 0;
	private String packetCode = "";
	private double sellingPrice = 0d;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPacketCode() {
		return packetCode;
	}
	public void setPacketCode(String packetCode) {
		this.packetCode = packetCode;
	}
	public double getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
}
