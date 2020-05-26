package com.luvbrite.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.DispatchSalesExt;
import com.luvbrite.model.DispatchUpdateDTO;

public interface IDispatchService {

	public List<DispatchSalesExt> listDispatches(Integer driverId, Integer dispatchId, Boolean cancelled,
			Boolean finished, Boolean notFinished, String q, String orderBy, String mode, String qSORTDIR,
			Integer currentPage, Integer deliveryRtId, Integer shopId) throws Exception;

	public ResponseEntity<CommonResponse> updatePacketInfo(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> assignDriver(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> cancelDispatch(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> markArrived(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> markSold(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> inOfficeOrderProcess(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> dateUpdate(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> pmtModeUpdate(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> tipUpdate(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> splitUpdate(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> recalculateDistance(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> closeTheseSales(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> reopenTheseSales(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> resetSale(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

}
