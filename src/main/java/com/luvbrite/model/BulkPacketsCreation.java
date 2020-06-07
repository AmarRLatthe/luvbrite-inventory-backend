package com.luvbrite.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BulkPacketsCreation implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -3766984368640961294L;

	private Double price;
	private Double weight;
	private Integer totalPackets;
	private Integer purchaseId;
	private Integer operatorId;
	private Integer shopId;
}
