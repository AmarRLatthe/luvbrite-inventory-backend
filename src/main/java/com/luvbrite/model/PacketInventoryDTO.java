package com.luvbrite.model;

public class PacketInventoryDTO {

	private int id = 0;
	private int purchaseId = 0;
	private int shopId = 0;
	private int salesId = 0;
	private int returnDetailsId = 0;
	private String packetCode = "";
	private String dateAdded = "";
	private String dateSold = "";

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(int purchaseId) {
		this.purchaseId = purchaseId;
	}
	public int getShopId() {
		return shopId;
	}
	public void setShopId(int shopId) {
		this.shopId = shopId;
	}
	public int getSalesId() {
		return salesId;
	}
	public void setSalesId(int salesId) {
		this.salesId = salesId;
	}
	public int getReturnDetailsId() {
		return returnDetailsId;
	}
	public void setReturnDetailsId(int returnDetailsId) {
		this.returnDetailsId = returnDetailsId;
	}
	public double getWeightInGrams() {
		return weightInGrams;
	}
	public void setWeightInGrams(double weightInGrams) {
		this.weightInGrams = weightInGrams;
	}
	public double getMarkedPrice() {
		return markedPrice;
	}
	public void setMarkedPrice(double markedPrice) {
		this.markedPrice = markedPrice;
	}
	public double getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	public String getPacketCode() {
		return packetCode;
	}
	public void setPacketCode(String packetCode) {
		this.packetCode = packetCode;
	}
	public String getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}
	public String getDateSold() {
		return dateSold;
	}
	public void setDateSold(String dateSold) {
		this.dateSold = dateSold;
	}
	private double weightInGrams = 0d;
	private double markedPrice = 0d;
	private double sellingPrice = 0d;






}
