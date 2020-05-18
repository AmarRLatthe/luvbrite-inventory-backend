package com.luvbrite.model;

public class ProductsExt  extends Products{

	private String categoryName = "";
	private String strainName = "";
	private Long mongo_productid;

	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getStrainName() {
		return strainName;
	}
	public void setStrainName(String strainName) {
		this.strainName = strainName;
	}
	@Override
	public Long getMongo_productid() {
		return mongo_productid;
	}
	@Override
	public void setMongo_productid(Long mongo_productid) {
		this.mongo_productid = mongo_productid;
	}

}
