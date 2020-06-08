package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class GetDailySalesDTO {

	private String lFormat;
	private String sFormat;
	private double price;
	private int count;
}
