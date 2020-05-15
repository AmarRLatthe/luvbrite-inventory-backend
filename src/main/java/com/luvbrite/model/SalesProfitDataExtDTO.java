package com.luvbrite.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class SalesProfitDataExtDTO {

	private String date;	
	private double total;
	private double purchaseTotal;
	private double sellingTotal;
	private List<SalesProfitDataDTO> salesProfitData;
	
	public void addSpData(SalesProfitDataDTO spd){
		if(salesProfitData==null){
			salesProfitData = new ArrayList<SalesProfitDataDTO>();
		}
		
		salesProfitData.add(spd);
	}
}
