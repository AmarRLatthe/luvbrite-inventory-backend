package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.DispatchSalesExt;

public interface IDispacthService {


	List<DispatchSalesExt> listDispatches(int driverId, int dispatchId, boolean cancelled, boolean finished,
			boolean notFinished, String q, String orderBy, String mode, String qSORTDIR, int currentPage,
			int deliveryRtId,int shopId) throws Exception;

}
