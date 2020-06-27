package com.luvbrite.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.CustomerDrillDownDTO;
import com.luvbrite.model.DispatchSalesExtDTO;
import com.luvbrite.model.OrderBreakDownDTO;
import com.luvbrite.model.SalesProfitDataExtDTO;
import com.luvbrite.model.googlechart.DataTable;
import com.luvbrite.repository.IStatisticsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StatisticsServiceImpl implements IStatisticsService {

	@Autowired
	private IStatisticsRepository iStatisticsRepository;

	@Override
	public List<OrderBreakDownDTO> getBaseStatisticsData(int shopId) {
		try {
			return iStatisticsRepository.getBaseStatsData(shopId);
		} catch (Exception e) {
			log.error("Exception while getting base stats inside statistic service : Message is {}, exception ",
					e.getMessage(), e);
			return Collections.emptyList();
		}

	}

	@Override
	public List<OrderBreakDownDTO> getProdStat(String startDate, String endDate) {
		try {
			return iStatisticsRepository.getProdStat(startDate, endDate);
		} catch (Exception e) {
			log.error("Exception while getting prod stats inside statistic service : Message is {}, exception ",
					e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<OrderBreakDownDTO> getStasDataByDriverId(String startDate, String endDate, String driverId) {
		try {
			return iStatisticsRepository.getStatsByDriverId(startDate, endDate, driverId);
		} catch (Exception e) {
			log.error("Exception while getting stats data by driver id inside statistic service : Message is {}, exception ",
					e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<OrderBreakDownDTO> getCustomerStatData(String startDate, String endDate) {
		try {
			return iStatisticsRepository.getCustomerStats(startDate, endDate);
		} catch (Exception e) {
			log.error("Exception while getting customer stats inside statistic service : Message is {}, exception ",
					e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<OrderBreakDownDTO> getOrderStatData(String startDate, String endDate, String showFirstOrder,
			String paymentMode) {
		try {
			return iStatisticsRepository.getOrderStats(startDate, endDate, showFirstOrder, paymentMode);
		} catch (Exception e) {
			log.error("Exception while getting order stats inside statistic service : Message is {}, exception ",
					e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<SalesProfitDataExtDTO> getSalesProfitInfo(String startDate, String endDate) {
		try {
			return iStatisticsRepository.getSalesProfitData(startDate, endDate);
		} catch (Exception e) {
			log.error("Exception while getting sales profit information inside statistic service : Message is {}, exception ",
					e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<CustomerDrillDownDTO> getCustomerStats(String startDate, String endDate) {
		try {
			return iStatisticsRepository.getCustomerDrillDownStat(startDate, endDate);
		} catch (Exception e) {
			log.error("Exception while getting customer drill down stats inside statistic service : Message is {}, exception ",
					e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public DataTable getDailySalesStat() {
		return iStatisticsRepository.getDailySalesStats();
	}

	@Override
	public List<DispatchSalesExtDTO> getDispatchSalesInfo(String startDate, String endDate) {
		try {
			return iStatisticsRepository.getListDispatches(startDate, endDate);
		} catch (Exception e) {
			log.error("Exception while getting dispatch sales info inside statistic service : Message is {}, exception ",
					e.getMessage(), e);
			return Collections.emptyList();
		}
	}
}
