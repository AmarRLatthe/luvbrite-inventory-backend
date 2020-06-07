package com.luvbrite.jdbcutils;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.Gson;
import com.luvbrite.model.DispatchSalesExt;
import com.luvbrite.model.OnlineOrderInfoDTO;
import com.luvbrite.model.TaxDetailsDTO;

public class DispatchSalesRowMapper implements RowMapper<DispatchSalesExt> {

	@Override
	public DispatchSalesExt mapRow(ResultSet rs, int rowNum) throws SQLException {

		DispatchSalesExt dispatch = new DispatchSalesExt();
		dispatch.setId(rs.getInt("dispatch_sales_id"));
		dispatch.setDriverId(rs.getInt("driver_id"));
		dispatch.setPriority(rs.getInt("priority"));
		dispatch.setPaymentMode(rs.getString("payment_mode"));
		dispatch.setCancellationReason(rs.getString("cancellation_reason"));

		if (rs.getString("cancellation_reason").trim().length() > 1) {
			dispatch.setPaymentMode("Cancelled - " + rs.getString("cancellation_reason"));
		}

		dispatch.setDateCalled(rs.getString("formatted_date_called"));
		dispatch.setDateArrived(rs.getString("formatted_date_arrived"));
		dispatch.setDateFinished(rs.getString("formatted_date_finished"));
		dispatch.setTotal_distance(rs.getString("distance_travelled"));
		dispatch.setDriverName(rs.getString("driver_name"));

		String tookan_driver_name = rs.getString("tookan_driver_name");

		if (!StringUtils.isEmpty(tookan_driver_name)) {
			dispatch.setDriverName(tookan_driver_name.trim());
		} else {
			dispatch.setDriverName(rs.getString("driver_name"));
		}
		String tookan_job_status = rs.getString("job_status");
		if (!StringUtils.isEmpty(tookan_job_status)) {

			switch (Integer.parseInt(tookan_job_status.trim())) {
			case 0:
				dispatch.setJob_status("Assigned");
				break;
			case 1:
				dispatch.setJob_status("Started");
				break;
			case 2:
				dispatch.setJob_status("Delivered");
				break;
			case 3:
				dispatch.setJob_status("Failed");
				break;
			case 4:
				dispatch.setJob_status("InProgress");
				break;

			case 6:
				dispatch.setJob_status("Unassigned");
				break;
			case 7:
				dispatch.setJob_status("Accepted");
				break;
			case 8:
				dispatch.setJob_status("Declined");
				break;
			case 9:
				dispatch.setJob_status("Cancelled");
				break;
			case 10:
				dispatch.setJob_status("Deleted");
				break;
			default:
				dispatch.setJob_status("");
				break;
			}

		}

		dispatch.setClientName(rs.getString("client_name"));
		dispatch.setClientName(rs.getString("client_name"));

		dispatch.setTip(rs.getDouble("tip"));
		dispatch.setCommissionPercent(rs.getDouble("commission_percent"));

		dispatch.setAdditionalInfo(rs.getString("additional_info"));
		dispatch.setSplitAmount(rs.getString("split_amount"));
		dispatch.setDistInMiles(rs.getDouble("dist_in_miles"));
		dispatch.setLat(rs.getDouble("lat"));
		dispatch.setLng(rs.getDouble("lng"));
		dispatch.setStatus(rs.getString("status"));

		dispatch.setTotalTaxApplied(rs.getDouble("total_tax_applied"));
		dispatch.setRushFeeApplied(rs.getDouble("rush_fee_applied"));

		dispatch.setDispatchDeliveryRelationId(rs.getInt("dr_id"));

		if (rs.getString("order_number") != null) {

			OnlineOrderInfoDTO ooi = new OnlineOrderInfoDTO();
			ooi.setAddress(rs.getString("address"));
			ooi.setPhone(rs.getString("phone"));
			ooi.setDispacthSalesId(rs.getInt("dispatch_sales_id"));
			ooi.setOrderNumber(rs.getString("order_number"));
			ooi.setTotal(rs.getDouble("total"));
			ooi.setNote(rs.getString("note"));
			ooi.setDeliveryNote(rs.getString("delivery_note"));
			ooi.setDiscount(rs.getDouble("total_discount"));
			ooi.setRushFee(rs.getDouble("rush_fee"));
			String taxInfo = rs.getString("tax_info");

			if ((taxInfo != null) && !taxInfo.equals("")) {
				TaxDetailsDTO td = new Gson().fromJson(taxInfo, TaxDetailsDTO.class);
				ooi.setTaxDetails(td);
			}

			Array productInfoS = rs.getArray("product_info");
			String[] productInfoA = (String[]) productInfoS.getArray();
			ooi.setProductInfo(java.util.Arrays.asList(productInfoA));

			dispatch.setOoi(ooi);
		}

		return dispatch;
	}

}
