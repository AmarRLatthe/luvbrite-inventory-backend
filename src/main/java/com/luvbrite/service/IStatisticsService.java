package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.CustomerDrillDownDTO;
import com.luvbrite.model.DispatchSalesExtDTO;
import com.luvbrite.model.OrderBreakDownDTO;
import com.luvbrite.model.SalesProfitDataExtDTO;
import com.luvbrite.model.googlechart.DataTable;


public interface IStatisticsService {

	public List<OrderBreakDownDTO> getBaseStatisticsData(int shopId);
	
	public List<OrderBreakDownDTO> getProdStat(String startDate, String endDate);
	
	public List<OrderBreakDownDTO> getStasDataByDriverId(String startDate, String endDate, String driverId);
	
	public List<OrderBreakDownDTO> getCustomerStatData(String startDate, String endDate);

	public List<OrderBreakDownDTO> getOrderStatData(String startDate, String endDate, String showFirstOrder,
			String paymentMode);

	public List<SalesProfitDataExtDTO> getSalesProfitInfo(String startDate, String endDate);
	
	public List<CustomerDrillDownDTO> getCustomerStats(String startDate, String endDate);
	
	public DataTable getDailySalesStat();
	
	public List<DispatchSalesExtDTO> getDispatchSalesInfo(String startDate, String endDate);
	
}
