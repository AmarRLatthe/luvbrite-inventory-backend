package com.luvbrite.model;

public class ProductDetailsDTO {

	private int product_id;
	private int category_id;
	private int strainid;
    private String categoryName;
	private String strain_name;
	private double total_purchase_qty;
	private double total_packet_qty;
	private double total_sold_qty;
	private double total_remain_qty;
	private double total_purchase_weight;
	private double total_packed_weight;
	private double total_sold_weight;
	private double total_remain_weight;
	private int returned;
	private int adjustment;

	public int getReturned() {
		return returned;
	}

	public void setReturned(int returned) {
		this.returned = returned;
	}

	public int getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(int adjustment) {
		this.adjustment = adjustment;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getStrain_name() {
		return strain_name;
	}

	public void setStrain_name(String strain_name) {
		this.strain_name = strain_name;
	}

	public double getTotal_purchase_qty() {
		return total_purchase_qty;
	}

	public void setTotal_purchase_qty(double total_purchase_qty) {
		this.total_purchase_qty = total_purchase_qty;
	}

	public double getTotal_packet_qty() {
		return total_packet_qty;
	}

	public void setTotal_packet_qty(double total_packet_qty) {
		this.total_packet_qty = total_packet_qty;
	}

	public double getTotal_sold_qty() {
		return total_sold_qty;
	}

	public void setTotal_sold_qty(double total_sold_qty) {
		this.total_sold_qty = total_sold_qty;
	}

	public double getTotal_remain_qty() {
		return total_remain_qty;
	}

	public void setTotal_remain_qty(double total_remain_qty) {

		// this.total_remain_qty =
		// this.total_packet_qty+this.adjustment-this.total_sold_qty-this.returned;
		this.total_remain_qty = total_remain_qty;

	}

	public double getTotal_purchase_weight() {
		return total_purchase_weight;
	}

	public void setTotal_purchase_weight(double total_purchase_weight) {
		this.total_purchase_weight = total_purchase_weight;
	}

	public double getTotal_packed_weight() {
		return total_packed_weight;
	}

	public void setTotal_packed_weight(double total_packed_weight) {
		this.total_packed_weight = total_packed_weight;
	}

	public double getTotal_sold_weight() {
		return total_sold_weight;
	}

	public void setTotal_sold_weight(double total_sold_weight) {
		this.total_sold_weight = total_sold_weight;
	}

	public double getTotal_remain_weight() {
		return total_remain_weight;
	}

	public int getStrainid() {
		return strainid;
	}

	public void setStrainid(int strainid) {
		this.strainid = strainid;
	}

	public void setTotal_remain_weight() {
		this.total_remain_weight = this.total_packed_weight - this.total_sold_weight;
	}
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setTotal_remain_weight(double total_remain_weight) {
		this.total_remain_weight = total_remain_weight;
	}

	@Override
	public String toString() {
		return "ProductDetails [\nproduct_id=" + product_id + ", \ncategory_id=" + category_id + ", \nstrainid="
				+ strainid + ", \nstrain_name=" + strain_name + ", \ntotal_purchase_qty=" + total_purchase_qty
				+ ", \ntotal_packet_qty=" + total_packet_qty + ", \ntotal_sold_qty=" + total_sold_qty
				+ ", \ntotal_remain_qty=" + total_remain_qty + ", \ntotal_purchase_weight=" + total_purchase_weight
				+ ", \ntotal_packed_weight=" + total_packed_weight + ", \ntotal_sold_weight=" + total_sold_weight
				+ ", \ntotal_remain_weight=" + total_remain_weight + "]";
	}
}
