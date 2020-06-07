package com.luvbrite.model;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class OrderBreakDownDTO {

	private String name;
	private double amount;
	private String mode;
	private double tip;
	private double commission;
	private double distance;
	private double odoDistance;
	
	private List<OrderStatisticDTO> ostat;
}
