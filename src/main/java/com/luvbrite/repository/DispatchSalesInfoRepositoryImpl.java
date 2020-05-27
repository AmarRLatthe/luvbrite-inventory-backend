package com.luvbrite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.maps.model.LatLng;
import com.luvbrite.externalservice.FindDistance;
import com.luvbrite.model.DispatchUpdateDTO;
import com.luvbrite.model.RoundTripDistanceDTO;
import com.luvbrite.model.SoldPacketsDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DispatchSalesInfoRepositoryImpl implements IDispatchSalesInfoRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public RoundTripDistanceDTO getLocationOfClient(int dispatchId) throws Exception {

		RoundTripDistanceDTO roundTripDTO = new RoundTripDistanceDTO();

		StringBuffer locationQuery = new StringBuffer();

		double distance = 0.0d;

		locationQuery.append("SELECT address FROM online_order_info WHERE dispatch_sales_id = ").append(dispatchId);

		String destination = jdbcTemplate.queryForObject(locationQuery.toString(), String.class);

		FindDistance findDistance = new FindDistance(destination);

		distance = 2 * distance;

		LatLng ll = findDistance.getLatLng();

		roundTripDTO.setLatLng(ll);

		roundTripDTO.setRoundTripDistance(distance);

		return roundTripDTO;
	}

	@Override
	public boolean updateDispatchSalesInfo(RoundTripDistanceDTO roundTripDistanceDetails,
			DispatchUpdateDTO dispatchUpdateDTO, String discountString) throws Exception {
		// TODO Auto-generated method stub

		StringBuffer updateDispatchQuery = new StringBuffer();

		updateDispatchQuery.append("UPDATE dispatch_sales_info ")
		.append("SET date_finished=to_timestamp(?,'MM/dd/yyyy HH:MI AM'), ")
		.append("payment_mode = ?, split_amount = ?, tip = ?, dist_in_miles = ?, ")
		.append("lat = ?, lng = ?, rush_fee_applied = ?, total_tax_applied = ? ").append(discountString)
		.append("WHERE id=?;");

		int rowUpdated = jdbcTemplate.update(updateDispatchQuery.toString(),
				new Object[] { dispatchUpdateDTO.getDatetime(), dispatchUpdateDTO.getPmtMode(),
						dispatchUpdateDTO.getSplit(), dispatchUpdateDTO.getTip(),
						roundTripDistanceDetails.getRoundTripDistance(), roundTripDistanceDetails.getLatLng().lat,
						roundTripDistanceDetails.getLatLng().lng, dispatchUpdateDTO.getRushFeeApplied(),
						dispatchUpdateDTO.getTaxApplied(), dispatchUpdateDTO.getId()

		});

		if (rowUpdated == 0) {
			log.error("dispatch_sales_info - date_finished, payment_mode update failed");
			return false;
		}

		return true;

	}

	@Override
	public boolean updateEachPacketsAsSold(SoldPacketsDTO sp) {


		StringBuffer upadatePacketInv = new StringBuffer();

		upadatePacketInv
		.append("UPDATE packet_inventory ")
		.append("SET date_sold=to_timestamp(?,'MM/dd/yyyy HH:MI AM'), sales_id = ?, selling_price = ? ")
		.append("WHERE id = ?");



		jdbcTemplate.update(upadatePacketInv.toString(), new Object[] {});
		pst = ncon.prepareStatement("UPDATE packet_inventory "
				+ "SET date_sold=to_timestamp(?,'MM/dd/yyyy HH:MI AM'), sales_id = ?, selling_price = ? "
				+ "WHERE id = ?");
		pst.setString(1, datetime);
		pst.setInt(2, id);
		salesIdlist.add(id);                    //Adding salesId to the list so that we can update products_available table

		pst.setDouble(3, sp.getSellingPrice());
		pst.setInt(4, sp.getId());


		return false;
	}









}
