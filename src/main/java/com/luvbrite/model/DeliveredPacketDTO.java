package com.luvbrite.model;

public class DeliveredPacketDTO {
	private int  items;
	private String productName;
	private int sales_id;
	private float total_tax_applied;
	private float total;
	private double sellingprice;

	public int getItems() {
		return items;
	}
	public void setItems(int items) {
		this.items = items;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getSales_id() {
		return sales_id;
	}
	public void setSales_id(int sales_id) {
		this.sales_id = sales_id;
	}
	public float getTotal_tax_applied() {
		return total_tax_applied;
	}
	public void setTotal_tax_applied(float total_tax_applied) {
		this.total_tax_applied = total_tax_applied;
	}
	public float getTotal() {
		return total;
	}
	public void setTotal(float total) {
		this.total = total;
	}
	public double getSellingprice() {
		return sellingprice;
	}
	public void setSellingprice(double sellingprice) {
		this.sellingprice = sellingprice;
	}
}
