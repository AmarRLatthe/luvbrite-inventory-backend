package com.luvbrite.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.CustomerDrillDownDTO;
import com.luvbrite.model.DispatchSalesExtDTO;
import com.luvbrite.model.OrderBreakDownDTO;
import com.luvbrite.model.SalesProfitDataExtDTO;
import com.luvbrite.model.UserDetails;
import com.luvbrite.model.googlechart.DataTable;
import com.luvbrite.service.IStatisticsService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/statistics/")
@Slf4j
public class StatisticsController {

	@Autowired
	private IStatisticsService iStatisticsService;

	@Autowired
	private IUserService iUserService;

	@GetMapping("/getbasestats")
	public ResponseEntity<CommonResponse> getBaseStatistics(Authentication authentication) {
		CommonResponse response = new CommonResponse();

		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<OrderBreakDownDTO> list = iStatisticsService.getBaseStatisticsData(userDetails.getShopId());
				if ((list != null) && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("something went wrong.please try again later");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Statistics data is not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getprodstat")
	public ResponseEntity<CommonResponse> getProductStat(@RequestParam String startDate, @RequestParam String endDate,
			Authentication authentication) {
		CommonResponse response = new CommonResponse();

		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<OrderBreakDownDTO> list = new ArrayList<>();
				list = iStatisticsService.getProdStat(startDate, endDate);
				if ((list != null) && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("Something went wrong. Please try again later");
				response.setData(list);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Prod statistics data is not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getstatbydriverid")
	public ResponseEntity<CommonResponse> getStatisticsByDriverId(@RequestParam String startDate,
			@RequestParam String endDate, @RequestParam String driverId, Authentication authentication) {
		CommonResponse response = new CommonResponse();

		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<OrderBreakDownDTO> list = new ArrayList<>();
				list = iStatisticsService.getStasDataByDriverId(startDate, endDate, driverId);
				if ((list != null) && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setData(list);
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("Something went wrong. Please try again later");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Statistics data by driver id is not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getorderstats")
	public ResponseEntity<CommonResponse> getOrderStats(@RequestParam String startDate, @RequestParam String endDate,
			@RequestParam String showFirstOrder, @RequestParam(required = false) String paymentMode,
			Authentication authentication) {
		CommonResponse response = new CommonResponse();

		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<OrderBreakDownDTO> list = new ArrayList<>();
				list = iStatisticsService.getOrderStatData(startDate, endDate, showFirstOrder, paymentMode);
				if ((list != null) && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setData(list);
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("Something went wrong. Please try again later");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Order statistics data is not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getcuststats")
	public ResponseEntity<CommonResponse> getCustStats(@RequestParam String startDate, @RequestParam String endDate,
			Authentication authentication) {
		CommonResponse response = new CommonResponse();

		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<OrderBreakDownDTO> list = new ArrayList<>();
				list = iStatisticsService.getCustomerStatData(startDate, endDate);
				if ((list != null) && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setData(list);
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("Something went wrong. Please try again later");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Customer statistics data is not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getsalesprofit")
	public ResponseEntity<CommonResponse> getSalesProfitData(@RequestParam String startDate,
			@RequestParam String endDate, Authentication authentication) {
		CommonResponse response = new CommonResponse();

		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<SalesProfitDataExtDTO> list = new ArrayList<>();
				list = iStatisticsService.getSalesProfitInfo(startDate, endDate);
				log.info("{} ", list);
				if ((list != null) && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(400);
				response.setData(list);
				response.setStatus("Bad Request");
				response.setMessage("Something went wrong. Please try again later");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Sales profit data is not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/getcustdrilldownstat")
	public ResponseEntity<CommonResponse> getCustomerDrillDownStats(@RequestParam String startDate,
			@RequestParam String endDate, Authentication authentication) {
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<CustomerDrillDownDTO> list = new ArrayList<>();
				list = iStatisticsService.getCustomerStats(startDate, endDate);
				if ((list != null) && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(400);
				response.setData(list);
				response.setStatus("Bad Request");
				response.setMessage("Something went wrong. Please try again later");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Customer drill down stats data is not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/getdailysalesstats")
	public ResponseEntity<CommonResponse> getDailySalesStats(Authentication authentication) {
		CommonResponse response = new CommonResponse();
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				DataTable dataTable = new DataTable();
				dataTable = iStatisticsService.getDailySalesStat();
				if (dataTable != null) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(dataTable);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("something went wrong.please try again later");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Daily sales stats not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@GetMapping(value = "/plotsalesmap")
	public ResponseEntity<CommonResponse> getPlotSalesMap(@RequestParam String startDate,
			@RequestParam String endDate, Authentication authentication) {
		
		CommonResponse response = new CommonResponse();
		
		try {
			UserDetails userDetails = iUserService.getByUsername(authentication.getName());
			if (userDetails != null) {
				List<DispatchSalesExtDTO> list =  new ArrayList<>();
				list = iStatisticsService.getDispatchSalesInfo(startDate, endDate);
				if ((list != null) && !list.isEmpty()) {
					response.setCode(200);
					response.setStatus("SUCCESS");
					response.setData(list);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setCode(400);
				response.setStatus("Bad Request");
				response.setMessage("something went wrong.please try again later");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setCode(401);
			response.setStatus("Unauthorized");
			response.setMessage("Please try to login and try again");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}" + e.getMessage(), e);
			response.setCode(500);
			response.setMessage("Daily sales stats not able to get. please try again later.");
			response.setStatus("SERVER ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
