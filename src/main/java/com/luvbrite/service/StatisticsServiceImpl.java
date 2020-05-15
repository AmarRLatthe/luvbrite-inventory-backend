package com.luvbrite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.OrderBreakDownDTO;
import com.luvbrite.model.SalesProfitDataExtDTO;
import com.luvbrite.repository.IStatisticsRepository;

@Service
public class StatisticsServiceImpl implements IStatisticsService {
	
	@Autowired
	private IStatisticsRepository iStatisticsRepository;

	@Override
	public List<OrderBreakDownDTO> getBaseStatisticsData(int shopId) {
		return iStatisticsRepository.getBaseStatsData(shopId);
	}

	@Override
	public List<OrderBreakDownDTO> getProdStat(String startDate, String endDate) {
		return iStatisticsRepository.getProdStat(startDate, endDate);
	}

	@Override
	public List<OrderBreakDownDTO> getStasDataByDriverId(String startDate, String endDate, String driverId) {
		return iStatisticsRepository.getStatsByDriverId(startDate, endDate, driverId);
	}

	@Override
	public List<OrderBreakDownDTO> getCustomerStatData(String startDate, String endDate) {
		return iStatisticsRepository.getCustomerStats(startDate, endDate);
	}

	@Override
	public List<OrderBreakDownDTO> getOrderStatData(String startDate, String endDate, String showFirstOrder,
			String paymentMode) {
		return iStatisticsRepository.getOrderStats(startDate, endDate, showFirstOrder, paymentMode);
	}
	
	@Override
	public List<SalesProfitDataExtDTO> getSalesProfitInfo(String startDate, String endDate) {
		return iStatisticsRepository.getSalesProfitData(startDate, endDate);
	}
	
	
}
