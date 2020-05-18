package com.luvbrite.model;

public class Products {

	private int id = 0;
	private String productName = "";
	private String nickName = "";
	private int categoryId = 0;
	private int strainId = 0;
	private long dateAdded = 0;
	private String formattedDateAdded = "";
	private String status = "";
	private Long mongo_productid = 0l;

	public Long getMongo_productid() {
		return mongo_productid;
	}

	public void setMongo_productid(Long mongo_productid) {
		this.mongo_productid = mongo_productid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getStrainId() {
		return strainId;
	}

	public void setStrainId(int strainId) {
		this.strainId = strainId;
	}

	public long getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(long dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getFormattedDateAdded() {
		return formattedDateAdded;
	}

	public void setFormattedDateAdded(String formattedDateAdded) {
		this.formattedDateAdded = formattedDateAdded;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Products [\nid=" + id + ", \nproductName=" + productName + ", \n nickName=" + nickName
				+ ", \ncategoryId=" + categoryId + ", \nstrainId=" + strainId + ", \ndateAdded=" + dateAdded
				+ ", \nformattedDateAdded=" + formattedDateAdded + ", \nstatus=" + status + "]";
	}
}
