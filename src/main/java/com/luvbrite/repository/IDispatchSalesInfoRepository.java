package com.luvbrite.repository;

import com.luvbrite.model.DispatchUpdateDTO;
import com.luvbrite.model.RoundTripDistanceDTO;
import com.luvbrite.model.SoldPacketsDTO;

public interface IDispatchSalesInfoRepository {


	RoundTripDistanceDTO getLocationOfClient(int dispatchId,int shopId) throws Exception;

	boolean updateDispatchSalesInfo(RoundTripDistanceDTO roundTripDistanceDetails, DispatchUpdateDTO dispatchUpdateDTO,String discountString,int shopId) throws Exception;

	boolean updatePacketsAsSold(SoldPacketsDTO sp,DispatchUpdateDTO dispatchUpdateDTO,int operatorId , int shopId);

	public boolean updateDate(DispatchUpdateDTO dispatchDto , int shopId);

	public boolean updatePacketSoldDate(String dateSold, int shopId);

	public boolean updateTip(int dispatchId,double tip, int shopId);

	public boolean resetDateFinished(int dispatchId , int shopId);

	public int resetPacketUpdate(int dispatchId , int shopId) ;

	public boolean updatePaymentMode(int dispatchId,String paymentMode , int shopId);

	public boolean updateSplitAmt(int dispatchId ,  int shopId );
}
