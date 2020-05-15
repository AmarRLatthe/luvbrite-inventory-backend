package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class SalesProfitDataDTO {

	private boolean flower;
	private double purchasePrice;
	private double sellingPrice;
	private double profit;
}
