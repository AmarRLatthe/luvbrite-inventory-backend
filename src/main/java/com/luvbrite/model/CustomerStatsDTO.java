package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class CustomerStatsDTO {

	private String clientName;
	private double amount;
	private int count;
	private String dateFormatted;
}
