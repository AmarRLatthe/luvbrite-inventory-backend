package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DriverStatDTO {

	private String driver;
	private String paymentMode;
	private int driverId;
	private String dispatchIds;
	private double amount;
	private int count;
	private String product;
}
