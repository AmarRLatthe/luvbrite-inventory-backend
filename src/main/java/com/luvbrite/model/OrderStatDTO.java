package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class OrderStatDTO {
	
	private String date;
	private double amount;
	private int count;
	private String label;
	private int dispatchId;
	private String extra1;
	private double originalTotal;
	private double tip = 0;
	private String status;
	private String note;
	private double totalTaxApplied;
	private double salesTax;
	private double exciseTax;
	private double totalDiscount;
	private String paymentMode;
	private String splitAmount;
}
