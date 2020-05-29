package com.luvbrite.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PurchasedOrderDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -4163744966706028493L;

	
	private String categoryName;
	private String condition;
	private Double inpackets;
	private Integer packetCount;
	private Integer purchaseCount;
	private String productName;
	private String purchaseDate;
	private Double purchased;
	private Integer remainingCount;
	private Double remainingPackets;
	private Double remainingTotal;
	private Double sold;
	private Integer soldCount;
	private String strainName;
	private String vendorName;
	
}

