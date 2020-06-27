package com.luvbrite.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShopInventoryDTO {

	private String productName = "";
	private String condition = "";

	private double purchased = 0d;
	private double inpackets = 0d;
	private double sold = 0d;
	private double returned = 0d;
	private double adjusted = 0d;
	private double remainingPackets = 0d;
	private double remainingTotal = 0d;
	private double inventoryCost = 0d; // Cost of items remaining in inventory
	private double weight = 0d;

	private int productId = 0;
	private int packetCount = 0;
	private int purchaseCount = 0;
	private int soldCount = 0;
	private int returnedCount = 0;
	private int remainingCount = 0;

	private boolean flower = false;

	
	
}
