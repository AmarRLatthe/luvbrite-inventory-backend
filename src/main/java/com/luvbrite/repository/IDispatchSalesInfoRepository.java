package com.luvbrite.repository;

import com.luvbrite.model.DispatchUpdateDTO;
import com.luvbrite.model.RoundTripDistanceDTO;
import com.luvbrite.model.SoldPacketsDTO;

public interface IDispatchSalesInfoRepository {


	RoundTripDistanceDTO getLocationOfClient(int dispatchId) throws Exception;

	boolean updateDispatchSalesInfo(RoundTripDistanceDTO roundTripDistanceDetails, DispatchUpdateDTO dispatchUpdateDTO,String discountString) throws Exception;

	boolean updateEachPacketsAsSold(SoldPacketsDTO sp);
}
