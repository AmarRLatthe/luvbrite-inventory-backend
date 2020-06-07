package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class CommonSalesProfitDTO {

	private String date;
	private double sellingPrice;
	private double purchasePrice;
	private int categoryId;
	private String categoryName;
}
