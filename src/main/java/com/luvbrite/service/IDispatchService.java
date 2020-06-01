package com.luvbrite.service;

import org.springframework.http.ResponseEntity;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.DispatchUpdateDTO;
import com.luvbrite.model.PaginatedDispatch;

public interface IDispatchService {

	public PaginatedDispatch listDispatches(Integer driverId, Integer dispatchId, Boolean cancelled,
			Boolean finished, Boolean notFinished, String q, String orderBy, String mode, String qSORTDIR,
			Integer currentPage, Integer deliveryRtId, Integer shopId) throws Exception;

	public ResponseEntity<CommonResponse> updatePacketInfo(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> assignDriver(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> cancelDispatch(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> markArrived(DispatchUpdateDTO dispatchUpdateDTO,int shopId) throws Exception;

	public  ResponseEntity<CommonResponse> markSold(DispatchUpdateDTO dispatchUpdateDTO,int operatorId,int shopId) throws Exception;

	public  ResponseEntity<CommonResponse> inOfficeOrderProcess(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> dateUpdate(DispatchUpdateDTO dispatchUpdateDTO,int shopId ,int operatorId) throws Exception;

	public ResponseEntity<CommonResponse> pmtModeUpdate(DispatchUpdateDTO dispatchUpdateDTO,int shopId,int operatorId) throws Exception ;

	public ResponseEntity<CommonResponse> tipUpdate(DispatchUpdateDTO dispatchUpdateDTO,int shopId,int operatorId) throws Exception;

	public ResponseEntity<CommonResponse> splitUpdate(DispatchUpdateDTO dispatchUpdateDTO,int shopId,int opsId) throws Exception;

	public  ResponseEntity<CommonResponse> recalculateDistance(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> closeTheseSales(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public  ResponseEntity<CommonResponse> reopenTheseSales(DispatchUpdateDTO dispatchUpdateDTO) throws Exception;

	public ResponseEntity<CommonResponse> resetSale(DispatchUpdateDTO dispatchUpdateDTO,int shopId,int operatorId) throws Exception;

}
