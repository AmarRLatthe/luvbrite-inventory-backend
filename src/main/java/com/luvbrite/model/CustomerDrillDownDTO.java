package com.luvbrite.model;

import java.util.ArrayList;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CustomerDrillDownDTO {

	private String customer;
	private double purchaseAmount;
	private int purchaseCount;
	
	private ArrayList<CustomerStatDrillDownDTO> cstats;
}
