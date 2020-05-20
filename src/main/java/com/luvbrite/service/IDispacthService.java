package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.DispatchSalesExt;

public interface IDispacthService {

	public List<DispatchSalesExt> listDispatches(Integer driverId, Integer dispatchId, Boolean cancelled,
			Boolean finished, Boolean notFinished, String q, String orderBy, String mode, String qSORTDIR,
			Integer currentPage, Integer deliveryRtId, Integer shopId) throws Exception;

	public boolean updatePacketInfo(int id, int opsId) throws Exception;

	public boolean assignDriver(int id, int opsId) throws Exception;

	public boolean cancelDispatch(int id, int opsId) throws Exception;

	public boolean markArrived(int id, int opsId) throws Exception;

	public boolean markSold(int id, int opsId) throws Exception;

	public boolean inOfficeOrderProcess(int id) throws Exception;

	public boolean dateUpdate(int id, int opsId) throws Exception;

	public boolean pmtModeUpdate(int id, int opsId) throws Exception;

	public boolean tipUpdate(int id, int opsId) throws Exception;

	public boolean splitUpdate(int id, int opsId) throws Exception;

	public boolean recalculateDistance(int id, int opsId) throws Exception;

	public boolean closeTheseSales(int opsId) throws Exception;

	public boolean reopenTheseSales(int opsId) throws Exception;

	public boolean resetSale(int id, int opsId) throws Exception;

}
