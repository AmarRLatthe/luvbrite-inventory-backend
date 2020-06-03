package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CustomerStatDrillDownDTO {

	private int count;
	private double amount;
	private String date;
}
