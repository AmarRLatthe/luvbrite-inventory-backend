package com.luvbrite.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PurchaseDTO {
	private int id = 0;
	private int productId = 0;
	private int quantity = 0;
	private int vendorId = 0;
	private int shop_id = 0;
	private double weightInGrams = 0d;
	private double unitPrice = 0d;
		
	private String operatorComments = "";
	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	private String growthCondition = "";
	private String dateAdded = "";
	private String purchaseCode = "";
	
	private String productName = "";
	private String vendorName = "";
	
	private int categoryId = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getVendorId() {
		return vendorId;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	public double getWeightInGrams() {
		return weightInGrams;
	}

	public void setWeightInGrams(double weightInGrams) {
		this.weightInGrams = weightInGrams;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getOperatorComments() {
		return operatorComments;
	}

	public void setOperatorComments(String operatorComments) {
		this.operatorComments = operatorComments;
	}

	public String getGrowthCondition() {
		return growthCondition;
	}

	public void setGrowthCondition(String growthCondition) {
		this.growthCondition = growthCondition;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getPurchaseCode() {
		return purchaseCode;
	}

	public void setPurchaseCode(String purchaseCode) {
		this.purchaseCode = purchaseCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

  public static void main(String[] args) throws JsonProcessingException {
	  ObjectMapper objectMapper = new ObjectMapper();
	  PurchaseDTO purchase = new PurchaseDTO();
	System.out.println(objectMapper.writeValueAsString(purchase)); 
  }
  
}
