package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.luvbrite.model.CommonSalesProfitDTO;
import com.luvbrite.model.DriverStatDTO;
import com.luvbrite.model.OrderBreakDownDTO;
import com.luvbrite.model.OrderStatDTO;
import com.luvbrite.model.OrderStatisticDTO;
import com.luvbrite.model.StatisticsDTO;
import com.luvbrite.model.Routine;
import com.luvbrite.model.SalesProfitDataDTO;
import com.luvbrite.model.SalesProfitDataExtDTO;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class StatisticsRepositoryImpl implements IStatisticsRepository {

	int dow = 0;
	int day = 0;
	int doy = 0;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private List<OrderBreakDownDTO> results = new ArrayList<>();

	@Override
	public List<OrderBreakDownDTO> getBaseStatsData(int shopId) {
		log.info("Inside getBaseStatsData to get base statistics data ");
		results = new ArrayList<>();
		StatisticsDTO dto = (StatisticsDTO) jdbcTemplate.queryForObject(getExtractQuery(),
				new BeanPropertyRowMapper(StatisticsDTO.class));
		dow = dto.getDow();
		day = dto.getDay();
		doy = dto.getDoy();
		/**
		 * DOW - The day of the week (SUN- SAT; 0 - 6; Sunday is 0) We want Monday to be
		 * Zero and the week to be Monday-Sunday
		 **/
		if (dow == 0)
			dow = 6;
		else
			dow = dow - 1;
		getData("t", shopId);
		getData("y", shopId);
		getData("wd", shopId);
		getData("w", shopId);
		getData("md", shopId);
		getData("m", shopId);
		getData("rd", shopId);

		return results;
	}

	@Override
	@Transactional
	public List<OrderBreakDownDTO> getStatsByDriverId(String startDate, String endDate, String driverId) {
		log.info("Inside get stats by driver id, for driver id {} ", driverId);
		List<OrderBreakDownDTO> results = new ArrayList<>();
		String previousDriver = "", currentDriver = "", previousPaymentMode = "", currentPaymentMode = "",
				dispatchId = "0";
		double groupAmount = 0d;

		boolean first = true;

		OrderBreakDownDTO obd = new OrderBreakDownDTO();
		OrderStatisticDTO or = new OrderStatisticDTO();

		int currentDriverId = 0, previousDriverId = 0;
		try {
			List<OrderStatisticDTO> orArray = new ArrayList<OrderStatisticDTO>();
			int driverID = 0;
			if (!StringUtils.isEmpty(driverId)) {
				driverID = Integer.parseInt(driverId);
			}
			List<DriverStatDTO> dtos = getDataBasedOnDriverId(driverId, startDate, endDate);
			for (DriverStatDTO dto : dtos) {
				currentDriver = dto.getDriver();
				currentPaymentMode = dto.getPaymentMode();
				currentDriverId = dto.getDriverId();

				if (!currentDriver.equals(previousDriver)) {
					if (!first) {
						obd.setName(previousDriver);
						obd.setAmount(groupAmount);
						obd.setMode(previousPaymentMode);
						obd.setOstat(orArray);

						setTip(dispatchId, obd);

						setDistance(previousDriverId, obd, startDate, endDate);

						obd.setCommission(getDriverCommision(dispatchId));

						results.add(obd);
						obd = new OrderBreakDownDTO();
						groupAmount = 0d;
						orArray = new ArrayList<>();
					} else {
						first = false;
					}

				} else if (!currentPaymentMode.equals(previousPaymentMode)) {
					obd.setName(currentDriver);
					obd.setAmount(groupAmount);
					obd.setMode(previousPaymentMode);
					obd.setOstat(orArray);

					setTip(dispatchId, obd);

					obd.setCommission(getDriverCommision(dispatchId));

					results.add(obd);

					obd = new OrderBreakDownDTO();
					groupAmount = 0d;
					orArray = new ArrayList<>();
				}

				double amount = dto.getAmount();
				groupAmount += amount;

				if (driverID > 0) {
					or = new OrderStatisticDTO();
					or.setCount(dto.getCount());
					or.setAmount(amount);
					or.setLabel(dto.getProduct());

					orArray.add(or);
				}

				previousDriver = currentDriver;
				previousDriverId = currentDriverId;
				previousPaymentMode = currentPaymentMode;
				dispatchId = dto.getDispatchIds();
			}
			if (!first) {
				obd.setName(previousDriver);
				obd.setAmount(groupAmount);
				obd.setMode(currentPaymentMode);
				obd.setOstat(orArray);

				setTip(dispatchId, obd);
				setDistance(previousDriverId, obd, startDate, endDate);

				obd.setCommission(getDriverCommision(dispatchId));

				results.add(obd);
			}
			return results;
		} catch (Exception e) {
			log.info("Exception while stats by driver id, driver id is {}, message is {}, exception is ", driverId,
					e.getMessage(), e);
		}
		return results;
	}

	@Override
	public List<OrderBreakDownDTO> getProdStat(String startDate, String endDate) {
		log.info("Inside get prodstat, to get statistics for period start date {}, end date {} ", startDate, endDate);
		List<OrderBreakDownDTO> results = new ArrayList<>();
		String cquery_where = "", GROUPBY = " GROUP BY p.product_name ";
		try {
			if (startDate.equals("")) {
				SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
				startDate = sd.format(Calendar.getInstance().getTime());
			}
			if (endDate.trim().equals("")) {
				cquery_where = " >= to_date('" + startDate + "', 'MM/dd/YYYY')";
			} else {
				cquery_where = " >= to_date('" + startDate + "', 'MM/dd/YYYY') " + "AND  pi.date_sold < to_date('"
						+ endDate + "', 'MM/dd/YYYY') + interval '1 day'";
			}
			StringBuilder prodStatQuery = new StringBuilder();
			prodStatQuery.append("SELECT COUNT(pi.id) AS count, SUM(pi.selling_price) AS amount, p.product_name  ")
					.append("FROM packet_inventory pi JOIN purchase_inventory pur ON pi.purchase_id = pur.id ")
					.append("JOIN products p ON p.id = pur.product_id JOIN dispatch_sales_info dis ON dis.id = pi.sales_id ")
					.append("WHERE pi.sales_id <> 0 AND dis.date_finished ").append(cquery_where).append(" ")
					.append(GROUPBY).append("ORDER BY amount DESC, product_name LIMIT 50");
			log.info("Executing prod stat query to get stats based of start and end date. Query is {}, "
					+ "start date {}, end date {} ", prodStatQuery, startDate, endDate);
			results = jdbcTemplate.query(prodStatQuery.toString(), new RowMapper<OrderBreakDownDTO>() {
				@Override
				public OrderBreakDownDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					OrderBreakDownDTO obd = new OrderBreakDownDTO();
					obd.setName(rs.getString("product_name"));
					obd.setMode(rs.getInt("count") + "");
					obd.setAmount(rs.getDouble("amount"));
					return obd;
				}
			});
			return results;
		} catch (Exception e) {
			log.error("Exception while fetching prod stat, message is {}, exception is ", e.getMessage(), e);
		}
		return results;
	}

	@Override
	public List<OrderBreakDownDTO> getOrderStats(String startDate, String endDate, String showFirstOrder,
			String paymentMode) {
		log.info("Inside ");
		boolean displayFirstOrders = Boolean.parseBoolean(showFirstOrder);
		List<OrderBreakDownDTO> results = new ArrayList<OrderBreakDownDTO>();
		String whereQuery = "", previousDate = "", currentDate = "",
				groupBy = " GROUP BY dis.date_finished, dis.id, dis.client_name, dis.payment_mode, dis.tip, driver, ooi.total, ooi.order_number,nds.tot_disc,ooi.note";

		String[] splitAmongCashNCredit;
		Double cashPayment = 0.0d;
		Double creditCardPayment = 0.0d;
		double groupAmount = 0d;

		boolean first = true;
		try {

			OrderBreakDownDTO obd = new OrderBreakDownDTO();
			OrderStatisticDTO or = new OrderStatisticDTO();
			boolean isPaymentModeProvided = false;
			ArrayList<OrderStatisticDTO> orArray = new ArrayList<>();

			if (endDate.trim().equals("")) {
				whereQuery = " >= to_date('" + startDate + "', 'MM/dd/YYYY')";
			} else {
				whereQuery = " >= to_date('" + startDate + "', 'MM/dd/YYYY') " + "AND  pi.date_sold < to_date('"
						+ endDate + "', 'MM/dd/YYYY') + interval '1 day'";
			}
			if (displayFirstOrders) {
				whereQuery = whereQuery + " AND  ooi.note = 'Payment Method: Donation on Delivery. **FIRST ORDER**'";
			}
			if ("Cash".equals(paymentMode)) {
				whereQuery = whereQuery + " AND  dis.payment_mode in ('Cash','Split') ";
				isPaymentModeProvided = true;
			} else if ("Credit Card".equals(paymentMode)) {
				whereQuery = whereQuery + " AND  dis.payment_mode in ('Credit Card','Split') ";
				isPaymentModeProvided = true;
			}

			StringBuilder netDiscountOnSalesTable = new StringBuilder();
			netDiscountOnSalesTable.append("WITH netDiscountOnSales AS (")
					.append(" SELECT sales_id , SUM(selling_price) as tot_disc ")
					.append(" FROM packet_inventory  WHERE selling_price < 0 GROUP BY sales_id  )");

			StringBuilder vQuery = new StringBuilder();
			vQuery.append(netDiscountOnSalesTable).append(
					" SELECT to_char(dis.date_finished, 'MM/dd/YYYY') AS date, dis.id, dis.client_name, dis.status,dis.split_amount,")
					.append("COUNT(pi.id) AS count, SUM(pi.selling_price) AS amount,nds.tot_disc, dis.total_tax_applied, dis.sales_tax, dis.excise_tax , ")
					.append("dis.payment_mode, dis.tip, COALESCE(ooi.total,0) AS original_total, ooi.order_number, ")
					.append("COALESCE(d.driver_name,'No Driver Assigned') AS driver , ooi.note FROM packet_inventory pi ")
					.append("JOIN purchase_inventory pur ON pi.purchase_id = pur.id ")
					.append("JOIN dispatch_sales_info dis ON dis.id = pi.sales_id ")
					.append("LEFT JOIN online_order_info ooi ON ooi.dispatch_sales_id = dis.id ")
					.append("LEFT JOIN drivers d ON d.id = dis.driver_id  ")
					.append("LEFT JOIN netDiscountOnSales nds ON nds.sales_id = dis.id ")
					.append("WHERE pi.sales_id <> 0 AND dis.date_finished ").append(whereQuery).append(groupBy)
					.append(" ORDER BY dis.date_finished ASC");
			List<OrderStatDTO> dtos = jdbcTemplate.query(vQuery.toString(), new RowMapper<OrderStatDTO>() {

				@Override
				public OrderStatDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					OrderStatDTO dto = new OrderStatDTO();
					dto.setDate(rs.getString("date"));
					dto.setAmount(rs.getDouble("amount"));
					dto.setCount(rs.getInt("count"));
					dto.setLabel(rs.getString("client_name") + " (" + rs.getString("order_number") + ")");
					dto.setDispatchId(rs.getInt("id"));
					dto.setExtra1(rs.getString("driver"));
					dto.setOriginalTotal(rs.getDouble("original_total"));
					dto.setTip(rs.getDouble("tip"));
					dto.setStatus(rs.getString("status"));
					dto.setNote(rs.getString("note"));
					dto.setTotalTaxApplied(rs.getDouble("total_tax_applied"));
					dto.setSalesTax(rs.getDouble("sales_tax"));
					dto.setExciseTax(rs.getDouble("excise_tax"));
					dto.setTotalDiscount(rs.getDouble("tot_disc"));
					dto.setPaymentMode(rs.getString("payment_mode"));
					dto.setSplitAmount(rs.getString("split_amount"));
					return dto;
				}
			});
			for (OrderStatDTO orderStatDTO : dtos) {
				currentDate = orderStatDTO.getDate();

				if (!currentDate.equals(previousDate)) {
					if (!first) {
						obd.setName(previousDate);
						obd.setAmount(groupAmount);
						obd.setOstat(orArray);

						results.add(obd);

						obd = new OrderBreakDownDTO();
						groupAmount = 0d;
						orArray = new ArrayList<>();
					} else {
						first = false;
					}
				}

				double amount = orderStatDTO.getAmount();
				groupAmount += amount;

				or = new OrderStatisticDTO();
				or.setCount(orderStatDTO.getCount());
				or.setAmount(amount);
				or.setLabel(orderStatDTO.getLabel());
				or.setDispatchId(orderStatDTO.getDispatchId());
				or.setExtra(orderStatDTO.getExtra1());

				/*
				 * or.setMode(rs.getString("payment_mode"));
				 * or.setOriginalTotal(rs.getDouble("original_total"));
				 */
				or.setOriginalTotal(orderStatDTO.getOriginalTotal());
				or.setTip(orderStatDTO.getTip());
				or.setStatus(orderStatDTO.getStatus());
				or.setNote(orderStatDTO.getNote());
				///

				or.setTotalTaxApplied(orderStatDTO.getTotalTaxApplied());
				if (or.getTotalTaxApplied() > 0
						&& ((orderStatDTO.getSalesTax() == 0.0d) && (orderStatDTO.getExciseTax() == 0.0d))) {

					or.setSalesTax(orderStatDTO.getTotalTaxApplied());

				} else {
					or.setExciseTax(orderStatDTO.getExciseTax());
					or.setSalesTax(orderStatDTO.getSalesTax());

				}

				or.setTotalDiscount(orderStatDTO.getTotalDiscount());

				double orderAmount = 0.0d;
				double total = 0.0d;

				if (orderStatDTO.getTotalDiscount() < 0.0d) {
					orderAmount = orderStatDTO.getAmount() + Math.abs(orderStatDTO.getTotalDiscount());
				} else if (orderStatDTO.getTotalDiscount() > 0.0d) {
					orderAmount = orderStatDTO.getAmount() + orderStatDTO.getTotalDiscount();
				} else {
					orderAmount = orderStatDTO.getAmount();
				}
				or.setOrderAmount(orderAmount);
				or.setSubtotal(orderStatDTO.getAmount()); // This amount = total price of packets + discount therefore
															// is
															// subtotal

				total = orderStatDTO.getTotalTaxApplied() + orderStatDTO.getAmount() + orderStatDTO.getTip();

				if (isPaymentModeProvided) {
					if ("Split".equals(orderStatDTO.getPaymentMode())) {
						if ("Cash".equals(paymentMode)) {
							if (!StringUtils.isEmpty(orderStatDTO.getSplitAmount())) {

								or.setMode(paymentMode + " - split");
								splitAmongCashNCredit = orderStatDTO.getSplitAmount().split("/");
								cashPayment = Double.parseDouble(
										splitAmongCashNCredit[0].replace("$", "").replace("CC", "").trim());
								or.setTotal(cashPayment);
							} else {
								or.setMode(orderStatDTO.getPaymentMode());
								or.setTotal(total);
							}

						} else if ("Credit Card".equals(paymentMode)) {
							if (!StringUtils.isEmpty(orderStatDTO.getSplitAmount())) {
								or.setMode(paymentMode + " - split");
								splitAmongCashNCredit = orderStatDTO.getSplitAmount().split("/");

								if (splitAmongCashNCredit.length == 1) {

									creditCardPayment = Double.parseDouble(
											splitAmongCashNCredit[0].replace("$", "").replace("CC", "").trim());
									or.setTotal(creditCardPayment);

								} else if (splitAmongCashNCredit.length == 2) {
									cashPayment = Double.parseDouble(
											splitAmongCashNCredit[0].replace("$", "").replace("CC", "").trim());

									creditCardPayment = Double.parseDouble(
											splitAmongCashNCredit[1].replace("$", "").replace("CC", "").trim());

									// or.setTotal(creditCardPayment);
									or.setTotal(total - cashPayment);
								}
							} else {
								or.setTotal(total);
							}
						}
					} else {
						or.setMode(orderStatDTO.getPaymentMode());
						or.setTotal(total);
					}
				} else {
					or.setMode(orderStatDTO.getPaymentMode());
					or.setTotal(total);
				}

				// or.setNote(rs.getString("note"));
				///

				orArray.add(or);

				previousDate = currentDate;
			}
			/**
			 * Add the last driver info.
			 *
			 */
			if (!first) {
				obd.setName(previousDate);
				obd.setAmount(groupAmount);
				obd.setOstat(orArray);

				results.add(obd);
			}
			return results;
		} catch (Exception e) {
			log.error("Exception while getting order breakdown info {} ", e);
		}

		return results;
	}

	@Override
	public List<OrderBreakDownDTO> getCustomerStats(String startDate, String endDate) {
		List<OrderBreakDownDTO> results = new ArrayList<OrderBreakDownDTO>();
		String whereQuery = "", groupBy = " GROUP BY dis.client_name ";

		try {

			if (endDate.trim().equals("")) {
				whereQuery = " >= to_date('" + startDate + "', 'MM/dd/YYYY')";
			} else {
				whereQuery = " >= to_date('" + startDate + "', 'MM/dd/YYYY') " + "AND  pi.date_sold < to_date('"
						+ endDate + "', 'MM/dd/YYYY') + interval '1 day'";
			}

			String vQuery = "SELECT dis.client_name, " + "COUNT(pi.id) AS count, SUM(pi.selling_price) AS amount  "
					+ "FROM packet_inventory pi " + "JOIN dispatch_sales_info dis ON dis.id = pi.sales_id "
					+ "WHERE pi.sales_id <> 0 AND dis.date_finished " + whereQuery + " " + groupBy
					+ "ORDER BY amount DESC";

			log.info("Executing query to get customer stats, query is {} ", vQuery);
			results = jdbcTemplate.query(vQuery.toString(), new RowMapper<OrderBreakDownDTO>() {
				@Override
				public OrderBreakDownDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					OrderBreakDownDTO obd = new OrderBreakDownDTO();
					obd.setName(rs.getString("client_name"));
					obd.setMode(rs.getInt("count") + "");
					obd.setAmount(rs.getDouble("amount"));
					return obd;
				}
			});
			return results;
		} catch (Exception e) {
			log.info("Exception while getting customer stats, message is {}, exception is {} ", e.getMessage(), e);
		}
		return results;
	}

	@Override
	public List<SalesProfitDataExtDTO> getSalesProfitData(String startDate, String endDate) {
		List<SalesProfitDataExtDTO> results = new ArrayList<SalesProfitDataExtDTO>();
		log.info("Inside get sales profit info ::::::::::::::: ");
		String dateQuery = "TO_CHAR(date_sold, 'YYYY - MM') AS date_formatted, ";
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyyy");

		try {

			Date startDt = sdf.parse(startDate);
			Date endDt;

			/**
			 * Date calculations
			 */
			if (endDate.trim().equals("")) {
				Calendar now = Calendar.getInstance();
				endDt = now.getTime();
				endDate = sdf.format(endDt);
			} else {
				endDt = sdf.parse(endDate);
			}

			/**
			 * Since we have to list so many data, we change the grouping as the date range
			 * increases. So if its < 20, we group by days if its < 140, we group by
			 * month/week if its >= 140, we group by year/month
			 *
			 */
			// Calculate the date difference
			long timeDiff = endDt.getTime() - startDt.getTime();
			long diffDays = timeDiff / (1000 * 60 * 60 * 24);
			if (diffDays < 20) {
				dateQuery = "TO_CHAR(pi.date_sold, 'MM/dd') AS date_formatted, ";
			} else if (diffDays < 140) {
				dateQuery = "TO_CHAR(pi.date_sold, 'YYYY-MM \"Week -\" W') AS date_formatted, ";
			}

			/**
			 * In the query we want to separate all flowers and non flowers - CASE
			 * p.category_id WHEN 1 THEN 1 ELSE 2 END AS category_id does that separation.
			 *
			 * For flowers, unitPrice * weightInGrams gives the purchasePrice While for
			 * non-floweres, its just unitPrice - CASE p.category_id WHEN 1 THEN
			 * pur.unit_price*pi.weight_in_grams ELSE pur.unit_price END takes care of that
			 *
			 * In the subquery, we apply the selection criteria and get our rows. The main
			 * query, we group the rows we got from subquery and find SUM of prices.
			 *
			 */
			/*
			 * Modification as of Dec 2016
			 * 
			 * After Nov 20th the flowers are added to the system as packets with SKU weight
			 * and price per SKU, so the unit purchase price will just unitPrice The date
			 * has been added to CASE to take care of this.
			 */

			StringBuilder salesProfitSubQuery = new StringBuilder();
			salesProfitSubQuery.append(" SELECT ").append(dateQuery)
					.append("category_id, pi.selling_price, c.category_name, CASE WHEN (p.category_id = 1 AND pur.date_added < '2016-11-30')")
					.append("THEN pur.unit_price*pi.weight_in_grams ELSE pur.unit_price END AS purchase_price ")
					.append("FROM packet_inventory pi JOIN purchase_inventory pur ON pur.id = pi.purchase_id ")
					.append("JOIN products p ON p.id = pur.product_id JOIN categories c ON c.id=p.category_id WHERE pi.sales_id <> 0 AND pi.date_sold >= to_date('")
					.append(startDate).append("', 'MM/dd/YYYY') ").append("AND  pi.date_sold < to_date('")
					.append(endDate).append("', 'MM/dd/YYYY') + interval '1 day'");

			SalesProfitDataExtDTO spdExt = new SalesProfitDataExtDTO();
			String currentDate = "", previousDate = "";
			double total = 0d, purchaseTotal = 0d, sellingTotal = 0d, profit = 0d;
			StringBuilder vQuery = new StringBuilder();
			vQuery.append("WITH sub AS (").append(salesProfitSubQuery).append(") SELECT date_formatted, category_id, category_name, ")
					.append("SUM(selling_price) AS selling_price, SUM(purchase_price) AS purchase_price FROM sub ")
					.append("GROUP BY date_formatted, category_id, category_name ORDER BY date_formatted ASC, category_id ASC");
			log.info("query ::::::::::::::::: {} ", vQuery.toString()); 
			List<CommonSalesProfitDTO> dtos = jdbcTemplate.query(vQuery.toString(),
					new RowMapper<CommonSalesProfitDTO>() {

						@Override
						public CommonSalesProfitDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
							CommonSalesProfitDTO dto = new CommonSalesProfitDTO();
							dto.setDate(rs.getString("date_formatted"));
							dto.setPurchasePrice(rs.getDouble("purchase_price"));
							dto.setSellingPrice(rs.getDouble("selling_price"));
							dto.setCategoryId(rs.getInt("category_id"));
							dto.setCategoryName(rs.getString("category_name"));
							return dto;
						}
					});
			for (CommonSalesProfitDTO salesProfitDTO : dtos) {
				currentDate = salesProfitDTO.getDate();

				if (!previousDate.equals(currentDate) && !previousDate.equals("")) {
					spdExt.setDate(previousDate);
					spdExt.setTotal(total);
					spdExt.setPurchaseTotal(purchaseTotal);
					spdExt.setSellingTotal(sellingTotal);

					results.add(spdExt);

					spdExt = new SalesProfitDataExtDTO();
					total = 0d;
					purchaseTotal = 0d;
					sellingTotal = 0d;
				}

				profit = salesProfitDTO.getSellingPrice() - salesProfitDTO.getPurchasePrice();

				purchaseTotal += salesProfitDTO.getPurchasePrice();
				sellingTotal += salesProfitDTO.getSellingPrice();

				total += profit;

				SalesProfitDataDTO spd = new SalesProfitDataDTO();
				spd.setFlower(salesProfitDTO.getCategoryId() == 1 ? true : false);
				spd.setPurchasePrice(salesProfitDTO.getPurchasePrice());
				spd.setSellingPrice(salesProfitDTO.getSellingPrice());
				spd.setProfit(profit);
				spd.setCategoryName(salesProfitDTO.getCategoryName());

				spdExt.addSpData(spd);

				previousDate = currentDate;
			}
			// Add the file item to the array
			if (!previousDate.equals("")) {
				spdExt.setDate(currentDate);
				results.add(spdExt);
				spdExt.setTotal(total);
				spdExt.setPurchaseTotal(purchaseTotal);
				spdExt.setSellingTotal(sellingTotal);
			}
			return results;
		} catch (Exception e) {
			log.info("Exception while getting sales profile data, message is: {}, exception is {} ", e.getMessage(), e);
		}
		return results;
	}

	private void getData(String param, int shopId) {
		log.info("Inside get data");
		StringBuilder cqueryWhere = new StringBuilder();
		String groupBy = " GROUP BY dis.payment_mode ";
		double total = 0d;
		int totalCount = 0;
		String dispatchIds = "";

		try {
			OrderBreakDownDTO odb = new OrderBreakDownDTO();

			/**
			 * We have to use day-1 or doy-1 when calculating Month to date, Last Month,
			 * Year to date.
			 * 
			 * Suppose its 2nd day of the month, then variable 'day' would be 2,
			 * (current_date - 2) would be last day of last month, and it will include all
			 * the transactions for that month. Same applies to doy.
			 * 
			 **/

			if (param.equals("t")) {
				cqueryWhere.append(" >= current_date ");
				odb.setName("Today");
			} else if (param.equals("y")) {
				cqueryWhere.append(" >= (current_date - interval '1 day') AND date_sold < current_date ");
				odb.setName("Yesterday");
			} else if (param.equals("wd")) {
				cqueryWhere.append(" >= (current_date - interval '" + dow + " days')");
				odb.setName("Week to Date");
			} else if (param.equals("w")) {
				cqueryWhere.append(" >= (current_date - interval '1 week " + dow);
				cqueryWhere.append(" days') AND date_sold < (current_date - interval '" + dow + " days')");
				odb.setName("Last Week");
			} else if (param.equals("md")) {
				cqueryWhere.append(" >= (current_date - interval '" + (day - 1) + " days')");
				odb.setName("Month to Date");
			} else if (param.equals("m")) {
				cqueryWhere.append(" >= (current_date - interval '1 month " + (day - 1));
				cqueryWhere.append(" days') AND date_sold < (current_date - interval '" + (day - 1) + " days')");
				odb.setName("Last Month");
			} else if (param.equals("rd")) {
				cqueryWhere.append(" >= (current_date - interval '" + (doy - 1) + " days')");
				odb.setName("Year to Date");
			} else
				return;

			StringBuilder vQuery = new StringBuilder();
			vQuery.append(
					"SELECT array_to_string(array_agg(dis.id), ',') As dis_ids, COUNT(DISTINCT pi.sales_id), SUM(pi.selling_price), ");
			vQuery.append("dis.payment_mode FROM packet_inventory pi ");
			vQuery.append("JOIN dispatch_sales_info dis ON dis.id = pi.sales_id WHERE sales_id <> 0 AND date_sold  ");
			vQuery.append(cqueryWhere + groupBy + "ORDER BY dis.payment_mode");
			log.info("Executing query for base stats, query is: {} ", vQuery);
			List<OrderStatisticDTO> dtos = jdbcTemplate.query(vQuery.toString(), new RowMapper<OrderStatisticDTO>() {
				@Override
				public OrderStatisticDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					OrderStatisticDTO or = new OrderStatisticDTO();
					or.setCountF(rs.getFloat(2));
					or.setAmount(rs.getDouble(3));
					or.setMode(rs.getString("payment_mode"));
					or.setDisIds(rs.getString("dis_ids"));
					return or;
				}
			});
			for (OrderStatisticDTO dto : dtos) {
				totalCount += dto.getCountF();
				total += dto.getAmount();
				if (dto.getMode().equals("Split")) {
					dispatchIds = dto.getDisIds();
				}
			}
			odb.setOstat(dtos);
			odb.setAmount(total);
			odb.setMode(totalCount + "");

			if (!dispatchIds.equals("")) {
				checkSplit(odb, dispatchIds);
			}
			results.add(odb);

		} catch (Exception e) {
			log.info("Exception while getting base stats based on shop id: {}, exception is: {}", shopId, e);
		}
	}

	private void checkSplit(OrderBreakDownDTO obd, String dispatchIds) {

		OrderStatisticDTO cash = new OrderStatisticDTO();
		OrderStatisticDTO credit = new OrderStatisticDTO();
		OrderStatisticDTO payPal = new OrderStatisticDTO();
		// OrderStatistic split = new OrderStatistic();

		cash.setMode("Cash");
		credit.setMode("Credit Card");
		payPal.setMode("Paypal");

		List<OrderStatisticDTO> ors = obd.getOstat();
		for (OrderStatisticDTO or : ors) {

			if (or.getMode().equals("Cash"))
				cash = or;

			else if (or.getMode().equals("Credit Card"))
				credit = or;

			else if (or.getMode().equals("Paypal"))
				payPal = or;

			/*
			 * else if(or.getMode().equals("Split")) split = or;
			 */
		}

		double splitCashT = cash.getAmount(), splitCreditT = credit.getAmount(), splitPaypalT = payPal.getAmount();

		float cashCnt = cash.getCountF(), creditCnt = credit.getCountF(), payPalCnt = payPal.getCountF();

		String[] splitAmount;
		try {
			StringBuilder query = new StringBuilder();
			query.append("SELECT split_amount FROM dispatch_sales_info ");
			query.append("WHERE id IN (");
			query.append(dispatchIds);
			query.append(")");
			List<String> str = jdbcTemplate.queryForList(query.toString(), String.class);
			for (String response : str) {
				splitAmount = response.split("/");
				double splitCash = 0d, splitCredit = 0d, splitPaypal = 0d;

				if (splitAmount.length == 3) {
					splitCash = Routine.getDouble(splitAmount[0]);
					splitCredit = Routine.getDouble(splitAmount[1]);
					splitPaypal = Routine.getDouble(splitAmount[2]);

					if (splitCash > 0 && splitCredit > 0 && splitPaypal > 0) {
						cashCnt += 0.333f;
						creditCnt += 0.333f;
						payPalCnt += 0.333f;
					} else if (splitCash > 0d && splitPaypal > 0d) {
						cashCnt += 0.5f;
						payPalCnt += 0.5f;
					} else if (splitCredit > 0d && splitPaypal > 0d) {
						creditCnt += 0.5f;
						payPalCnt += 0.5f;
					} else if (splitCash > 0d && splitCredit > 0d) { // this scenario shouldn't come ideally!
						cashCnt += 0.5f;
						creditCnt += 0.5f;
					}
				} else if (splitAmount.length == 2) {
					splitCash = Routine.getDouble(splitAmount[0]);
					splitCredit = Routine.getDouble(splitAmount[1]);

					if (splitCash > 0d && splitCredit > 0d) {
						cashCnt += 0.5f;
						creditCnt += 0.5f;
					} else if (splitCredit > 0d) { // this scenario shouldn't come ideally!
						creditCnt += 1f;
					} else if (splitCash > 0d) { // this scenario shouldn't come ideally!
						cashCnt += 1f;
					}
				}

				else if (splitAmount.length == 1) {
					splitCash = Routine.getDouble(splitAmount[0]);

					if (splitCash > 0d) {
						cashCnt += 1f;
					}
				}

				splitCashT += splitCash;
				splitCreditT += splitCredit;
				splitPaypalT += splitPaypal;
			}

			ors = new ArrayList<OrderStatisticDTO>();
			if (splitCashT > 0d) {
				cash.setAmount(splitCashT);
				cash.setCountF(cashCnt);

				ors.add(cash);
			}

			if (splitCreditT > 0d) {
				credit.setAmount(splitCreditT);
				credit.setCountF(creditCnt);

				ors.add(credit);
			}

			if (splitPaypalT > 0d) {
				payPal.setAmount(splitPaypalT);
				payPal.setCountF(payPalCnt);

				ors.add(payPal);
			}

			obd.setOstat(ors);

		} catch (Exception e) {

		}
	}

	private double setTip(String dispatchIds, OrderBreakDownDTO obd) {
		double tip = 0d;
		try {
			tip = jdbcTemplate.queryForObject(
					"SELECT SUM(tip) FROM dispatch_sales_info WHERE id IN (" + dispatchIds + ")", Double.class);
			obd.setTip(tip);
		} catch (Exception e) {
			log.error("Exception while getting sum(tip), exception is {} ", e);
		}
		return tip;
	}

	private void setDistance(int driverId, OrderBreakDownDTO obd, String startDate, String endDate) {

		String cquery_where1 = "", cquery_where2 = "";
		try {
			if (endDate.trim().equals("")) {
				cquery_where1 = cquery_where2 = " >= to_date('" + startDate + "', 'MM/dd/YYYY')";
			} else {
				cquery_where1 = " >= to_date('" + startDate + "', 'MM/dd/YYYY') " + "AND  date_completed < to_date('"
						+ endDate + "', 'MM/dd/YYYY') + interval '1 day'";
				cquery_where2 = " >= to_date('" + startDate + "', 'MM/dd/YYYY') " + "AND  log_date < to_date('"
						+ endDate + "', 'MM/dd/YYYY') + interval '1 day'";
			}
			String distanceQuery = "SELECT SUM(distance_calculated) FROM delivery_routes WHERE driver_id = ? "
					+ "AND date_completed " + cquery_where1;
			String odoDistanceQuery = "SELECT SUM(distance) FROM driver_odometer_log WHERE driver_id = ?"
					+ " AND log_date " + cquery_where2;
			double distance = 0d;
			double odoDistance = 0d;
			try {
				distance = jdbcTemplate.queryForObject(distanceQuery, new Object[] { driverId }, double.class);
				odoDistance = (double) jdbcTemplate.queryForObject(odoDistanceQuery, new Object[] { driverId },
						double.class);
			} catch (Exception e) {

			}
			obd.setDistance(distance);
			obd.setOdoDistance(odoDistance);
		} catch (Exception e) {
			log.error("Exception while getting distance and odoDistance {} ", e);
		}
	}

	private double getDriverCommision(String dispatchIds) {
		double commission = 0d;
		try {
			StringBuilder driverCommissionQuery = new StringBuilder();
			driverCommissionQuery.append("SELECT SUM((pi.selling_price * dis.commission_percent) /100) ")
					.append("FROM packet_inventory pi JOIN dispatch_sales_info dis ON dis.id = pi.sales_id  ")
					.append("WHERE dis.id IN ( ").append(dispatchIds).append(" )");
			commission = jdbcTemplate.queryForObject(driverCommissionQuery.toString(), Double.class);
		} catch (Exception e) {
			log.error("Exception while getting driver commission {} ", e);
		}
		return commission;
	}

	private String getExtractQuery() {
		StringBuilder query = new StringBuilder();
		query.append("SELECT EXTRACT(DOW FROM now())::integer as dow, ")
				.append("EXTRACT(DOY FROM now())::integer as doy, ").append("EXTRACT(DAY FROM now())::integer as day");
		return query.toString();
	}

	public List<DriverStatDTO> getDataBasedOnDriverId(String driverId, String startDate, String endDate) {

		String whereQuery = "", SELECTS = "", groupBy = " GROUP BY d.id, d.driver_name, dis.payment_mode ";

		if (endDate.trim().equals("")) {
			whereQuery = " >= to_date('" + startDate + "', 'MM/dd/YYYY')";
		} else {
			whereQuery = " >= to_date('" + startDate + "', 'MM/dd/YYYY') " + "AND  pi.date_sold < to_date('" + endDate
					+ "', 'MM/dd/YYYY') + interval '1 day'";
		}
		int driverID = 0;
		if (!StringUtils.isEmpty(driverId)) {
			driverID = Integer.parseInt(driverId);
		}
		if (driverID > 0) {
			SELECTS = "p.product_name As product, ";
			groupBy += ", p.product_name ";
			whereQuery += " AND dis.driver_id = " + driverId;
		}
		StringBuilder statQueryByDriverId = new StringBuilder();
		statQueryByDriverId.append("SELECT array_to_string(array_agg(dis.id), ',') As dis_ids, COUNT(pi.id) AS count, ")
				.append("SUM(pi.selling_price) AS amount, dis.payment_mode, ").append(SELECTS)
				.append("COALESCE(d.driver_name,'No Driver Assigned') AS driver, d.id AS driver_id ")
				.append("FROM packet_inventory pi JOIN purchase_inventory pur ON pi.purchase_id = pur.id ")
				.append("JOIN products p ON p.id = pur.product_id JOIN dispatch_sales_info dis ON dis.id = pi.sales_id ")
				.append("LEFT JOIN drivers d ON d.id = dis.driver_id WHERE sales_id <> 0 AND pi.date_sold ")
				.append(whereQuery).append(" ").append(groupBy).append("ORDER BY d.driver_name, dis.payment_mode");
		log.info("Executing query to get stats by driver id, query is: {} driver id provided is: {} ",
				statQueryByDriverId, driverID);
		List<DriverStatDTO> dtos = null;
		if (driverID > 0) {
			dtos = jdbcTemplate.query(statQueryByDriverId.toString(), new RowMapper<DriverStatDTO>() {
				@Override
				public DriverStatDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					DriverStatDTO dto = new DriverStatDTO();
					dto.setDriver(rs.getString("driver"));
					dto.setPaymentMode(rs.getString("payment_mode"));
					dto.setDriverId(rs.getInt("driver_id"));
					dto.setDispatchIds(rs.getString("dis_ids"));
					dto.setAmount(rs.getDouble("amount"));
					dto.setCount(rs.getInt("count"));
					dto.setProduct(rs.getString("product"));
					return dto;
				}
			});
		} else {
			dtos = jdbcTemplate.query(statQueryByDriverId.toString(), new RowMapper<DriverStatDTO>() {
				@Override
				public DriverStatDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					DriverStatDTO dto = new DriverStatDTO();
					dto.setDriver(rs.getString("driver"));
					dto.setPaymentMode(rs.getString("payment_mode"));
					dto.setDriverId(rs.getInt("driver_id"));
					dto.setDispatchIds(rs.getString("dis_ids"));
					dto.setAmount(rs.getDouble("amount"));
					dto.setCount(rs.getInt("count"));
					return dto;
				}
			});
		}
		return dtos;
	}
}
