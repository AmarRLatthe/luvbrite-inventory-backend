package com.luvbrite.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispatchSalesExt extends DispatchSalesInfo {

	private String driverName;
	private int dispatchDeliveryRelationId;
	private OnlineOrderInfoDTO ooi = null;
	private String job_status;
	private String total_distance;

}
