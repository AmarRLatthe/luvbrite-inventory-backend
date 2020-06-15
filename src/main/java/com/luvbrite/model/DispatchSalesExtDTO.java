package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DispatchSalesExtDTO {

	private int id;
	private Double distInMiles = 0d;
	private Double lat = 0d;
	private Double lng = 0d;
}
