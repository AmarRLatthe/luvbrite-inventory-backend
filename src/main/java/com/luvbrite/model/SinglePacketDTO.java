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
public class SinglePacketDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 4480918645522548007L;

	private String productName;
	private String sku;
	private Double weight;
	private Double price;
	private Integer id;
	private Integer operatorId;
	private String mode;
	private Integer purchaseId;
	private Integer shopId;
	
	
}
