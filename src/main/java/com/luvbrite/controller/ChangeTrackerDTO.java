package com.luvbrite.controller;

public class ChangeTrackerDTO {

	private int id = 0;
	private int itemId = 0;
	private int operatorId = 0;
	private String actionType = "";
	private String actionOn = "";
	private String actionDetails = "";
	private int shopId;

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	private String date = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActionOn() {
		return actionOn;
	}

	public void setActionOn(String actionOn) {
		this.actionOn = actionOn;
	}

	public String getActionDetails() {
		return actionDetails;
	}

	public void setActionDetails(String actionDetails) {
		this.actionDetails = actionDetails;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
