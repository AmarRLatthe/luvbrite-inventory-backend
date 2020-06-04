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
	public RoundTripDistanceDTO getLocationOfClient(int dispatchId, int shopId) throws Exception {

		RoundTripDistanceDTO roundTripDTO = new RoundTripDistanceDTO();

		StringBuffer locationQuery = new StringBuffer();

		double distance = 0.0d;

		locationQuery.append("SELECT address FROM online_order_info WHERE dispatch_sales_id = ").append("?")
		.append("AND shop_id = ?");

		String destination = jdbcTemplate.queryForObject(locationQuery.toString(), new Object[] { dispatchId, shopId },
				String.class);

		FindDistance findDistance = new FindDistance(destination);

		distance = 2 * distance;

		LatLng ll = findDistance.getLatLng();

		roundTripDTO.setLatLng(ll);

		roundTripDTO.setRoundTripDistance(distance);

		return roundTripDTO;
	}

	@Override
	public boolean updateDispatchSalesInfo(RoundTripDistanceDTO roundTripDistanceDetails,
			DispatchUpdateDTO dispatchUpdateDTO, String discountString, int shopId) throws Exception {
		// TODO Auto-generated method stub

		StringBuffer updateDispatchQuery = new StringBuffer();

		updateDispatchQuery.append("UPDATE dispatch_sales_info ")
		.append("SET date_finished=to_timestamp(?,'MM/dd/yyyy HH:MI AM'), ")
		.append("payment_mode = ?, split_amount = ?, tip = ?, dist_in_miles = ?, ")
		.append("lat = ?, lng = ?, rush_fee_applied = ?, total_tax_applied = ? ").append(discountString)
		.append("WHERE id=?;").append(" AND shop_id = ?");

		int rowUpdated = jdbcTemplate.update(updateDispatchQuery.toString(),
				new Object[] { dispatchUpdateDTO.getDatetime(), dispatchUpdateDTO.getPmtMode(),
						dispatchUpdateDTO.getSplit(), dispatchUpdateDTO.getTip(),
						roundTripDistanceDetails.getRoundTripDistance(), roundTripDistanceDetails.getLatLng().lat,
						roundTripDistanceDetails.getLatLng().lng, dispatchUpdateDTO.getRushFeeApplied(),
						dispatchUpdateDTO.getTaxApplied(), dispatchUpdateDTO.getId(), shopId

		});

		if (rowUpdated == 0) {
			log.error("dispatch_sales_info - date_finished, payment_mode update failed");
			return false;
		}

		return true;

	}

	@Override
	public boolean updatePacketsAsSold(SoldPacketsDTO sp, DispatchUpdateDTO dispatchUpdateDTO, int operatorId,
			int shopId) {

		StringBuffer upadatePacketInv = new StringBuffer();

		upadatePacketInv.append("UPDATE packet_inventory ")
		.append("SET date_sold=to_timestamp(?,'MM/dd/yyyy HH:MI AM'), sales_id = ?, selling_price = ? ")
		.append("WHERE id = ?").append(" AND shop_id = ?");

		int rowsUpdated = jdbcTemplate.update(upadatePacketInv.toString(),
				new Object[] { dispatchUpdateDTO.getDatetime(), dispatchUpdateDTO.getDispatchId(), sp.getSellingPrice(),
						sp.getId(), shopId });

		if (rowsUpdated == 0) {
			log.error("packet_inventory - date_sold, sales_id update failed PacketsCode = " + sp.getPacketCode()
			+ ". OperatorId  " + operatorId);
			return false;
		}

		return true;
	}

	@Override
	public boolean updateDate(DispatchUpdateDTO dispatchDto, int shopId) {

		StringBuffer updateDate = new StringBuffer();

		updateDate.append("UPDATE dispatch_sales_info ")
		.append("SET date_finished=to_timestamp(?,'MM/dd/yyyy HH:MI AM') ").append("WHERE id=?")
		.append("AND shop_id = ?");

		int rowsUpdated = jdbcTemplate.update(updateDate.toString(), new Object[] { dispatchDto.getId(), shopId });

		if (rowsUpdated == 0) {
			log.error("Could not update dateArrived for dispatch_saes_info {} and shopId {} " + dispatchDto.getId()
			+ " " + shopId);
			return false;
		}

		return true;

	}

	@Override
	public boolean updatePacketSoldDate(String dateSold, int shopId) {

		StringBuffer updatePacketSoldDate = new StringBuffer();

		updatePacketSoldDate.append("UPDATE packet_inventory ")
		.append("SET date_sold=to_timestamp(?,'MM/dd/yyyy HH:MI AM') ").append("WHERE sales_id = ?")
		.append("AND shop_id = ?");

		int rowsUpdated = jdbcTemplate.update(updatePacketSoldDate.toString(), new Object[] { dateSold, shopId });

		if (rowsUpdated == 0) {
			log.error("Could not update packet date sold date to " + dateSold + "for shopId : " + shopId);
			return false;
		}

		log.info("Successfully updated packet sold date to " + dateSold + " for shopId " + shopId);
		return true;
	}

	@Override
	public boolean updateTip(int dispatchId, double tip, int shopId) {

		StringBuffer updateTip = new StringBuffer();

		updateTip.append("UPDATE dispatch_sales_info ").append("SET tip = ? ").append("WHERE id=?")
		.append("shop_id = ?");

		int rowsUpdated = jdbcTemplate.update(updateTip.toString(), new Object[] { tip, dispatchId, shopId });

		if (rowsUpdated == 0) {
			log.error("Could not update tip = " + tip + " for dispacthId = " + dispatchId + " and shopId = " + shopId);
			return false;
		}

		return true;

	}


	@Override
	public boolean resetDateFinished(int dispatchId , int shopId) {


		StringBuffer resetSale =  new StringBuffer();

		resetSale
		.append("UPDATE dispatch_sales_info ")
		.append("SET date_finished = NULL ")
		.append("WHERE id  = ?")
		.append(" AND shop_id = ?");


		int rowsUpdated = jdbcTemplate.update(resetSale.toString(), new Object[] {dispatchId,shopId});

		if(rowsUpdated == 0) {
			log.error("dispatch sales reset update failed");
			return false;
		}


		return true;
	}


	@Override
	public int resetPacketUpdate(int dispatchId , int shopId) {

		StringBuffer resetPacketUpdate = new StringBuffer();
		resetPacketUpdate.append("UPDATE packet_inventory ")
		.append("SET date_sold=NULL, sales_id = 0, selling_price = 0 ")
		.append("WHERE sales_id = ?")
		.append(" AND shop_id = ?");

		int rowsUpdated = 	jdbcTemplate.update(resetPacketUpdate.toString(), new Object[] {dispatchId,shopId});

		if(rowsUpdated == 0) {
			log.error("could not reset packets for salesID = "+dispatchId+" for shopId = "+shopId);
			return rowsUpdated;
		}

		return rowsUpdated;
	}


	@Override
	public boolean updatePaymentMode(int dispatchId,String paymentMode , int shopId) {


		StringBuffer updatePaymentMode =  new StringBuffer();
		updatePaymentMode.append("UPDATE dispatch_sales_info ")
		.append("SET payment_mode = ? ")
		.append("WHERE id=?")
		.append("shop_id = ?");

		int rowsUpdated = jdbcTemplate.update(updatePaymentMode.toString(), new Object[] {dispatchId,paymentMode,shopId});

		if(rowsUpdated==0) {
			log.error("could not updaet payment mode  to "+paymentMode+" for dispatch "+dispatchId+" for shop "+shopId);
			return false;
		}

		return true;

	}

	@Override
	public boolean updateSplitAmt(int dispatchId ,  int shopId ) {

		StringBuffer updateSplitAmt =  new StringBuffer();
		updateSplitAmt.append("UPDATE dispatch_sales_info ")
		.append("SET split_amount = ? ")
		.append("WHERE id=?")
		.append(" AND shop_id = ?");

		int rowsUpdated = 	jdbcTemplate.update(updateSplitAmt.toString(), new Object[] {dispatchId,shopId});

		if(rowsUpdated == 0) {
			log.error("could not update split amount for shopId = "+shopId+" dispatchId = "+dispatchId);
			return false;
		}

		return true;
	}



	@Override
	public	boolean updateStatusToClose(String sids,int shopId) {

		String saleIds = sids;

		//		if ((saleIds == null) || (saleIds.split(",").length < 1)) {
		//			log.info("Invalid SalesIds");
		//			//message = "Invalid Sale ids";
		//			return false;
		//		}

		StringBuffer closeSalesQuery =  new StringBuffer();

		closeSalesQuery.append("UPDATE dispatch_sales_info ")
		.append("SET status = 'locked' ")
		.append("WHERE id IN (" + saleIds + ");").
		append("AND shop_id = ?");


		int  rowsUpdated = jdbcTemplate.update(closeSalesQuery.toString(), new Object[] {shopId});


		//System.out.println("Update Dispatch - dispatch_sales_info update. Q - " + pst);
		if (rowsUpdated == 0) {
			//message = "dispatch_sales_info - status update failed";
			log.error("dispatch_sales_info - status update failed. Q - ");
			return false;
		}


		return true;

	}



	@Override
	public boolean updateStatusToOpen(String sids, int shopId) {

		String saleIds = sids;

		StringBuffer updateStatusToOpenQuery =  new StringBuffer();

		updateStatusToOpenQuery.append("UPDATE dispatch_sales_info ")
		.append("SET status = 'open' ")
		.append("WHERE id IN (" + saleIds + ");")
		.append("AND ")
		.append("shop_id = ?");


		int rowsUpdated = 	jdbcTemplate.update(updateStatusToOpenQuery.toString(), new Object[] {shopId});

		if(rowsUpdated==0) {
			log.error("dispatch_sales_info - status update failed");
			return false;
		}

		return true;
	}





}
