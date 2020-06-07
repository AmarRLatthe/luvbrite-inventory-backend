package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.CustomerDrillDownDTO;
import com.luvbrite.model.OrderBreakDownDTO;
import com.luvbrite.model.SalesProfitDataExtDTO;


public interface IStatisticsRepository {

	public List<OrderBreakDownDTO> getBaseStatsData(int shopId);
	
	public List<OrderBreakDownDTO> getProdStat(String startDate, String endDate);
	
	public List<OrderBreakDownDTO> getStatsByDriverId(String startDate, String endDate, String driverId);
	
	public List<OrderBreakDownDTO> getOrderStats(String startDate, String endDate, String showFirstOrder,
			String paymentMode);
	
	public List<OrderBreakDownDTO> getCustomerStats(String startDate, String endDate);
	
	public List<SalesProfitDataExtDTO> getSalesProfitData(String startDate, String endDate);
	
	public List<CustomerDrillDownDTO> getCustomerDrillDownStat(String startDate, String endDate);
}
