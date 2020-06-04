package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luvbrite.model.ChangeTrackerDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddMiscPackets {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	Tracker tracker;

	public AddMiscPackets() {
	}

	public boolean addPackets(String description, String saleDate, double amount, int opsId, int salesId, int shopId) {

		String logMsg = "New MiscPacket added";

		try {

			/**
			 * Purchase ID 202316 is already created for Misc Adjustments and is hard coded
			 * here.
			 *
			 * To make packetcode unique, random number is generated and added prepended to
			 * the description
			 **/

			int purchaseId = 202316;

			double rand = Math.random();
			String packetCode = "MISC" + Double.valueOf(rand * 1000000).intValue() + ">>" + description;

			if (description.indexOf("PROMO") == 0) {
				purchaseId = 203615; // when its PROMOCRTSY

				switch (description) {

				case "PROMOCPN":
					purchaseId = 203607;
					break;
				case "PROMODBLDN":
					purchaseId = 203613;
					break;
				case "PROMOKIVA":
					purchaseId = 203614;
					break;
				case "PROMOSEASNL":
					purchaseId = 202315;
					break;
				default:
					purchaseId = 203615;
					break;

				}

				packetCode = description + ">>" + Double.valueOf(rand * 1000000).intValue();
				logMsg = "Promocode added to sale";
			}

			StringBuffer insertIntoPacket = new StringBuffer();
			insertIntoPacket.append("INSERT INTO ")
			.append("packet_inventory(purchase_id, packet_code, selling_price, date_sold, sales_id,shop_id) ")
			.append("VALUES (?, ?, ?, to_timestamp(?,'MM/dd/yyyy HH:MI AM'), ?);");

			int rowsUpdated = jdbcTemplate.update(insertIntoPacket.toString(),
					new Object[] { purchaseId, packetCode, amount, saleDate, shopId });

			if (rowsUpdated > 0) {

				ChangeTrackerDTO ct = new ChangeTrackerDTO();
				ct.setActionDetails(logMsg + ". Amount: " + amount);
				ct.setActionType("update");
				ct.setActionOn("dispatch");
				ct.setItemId(salesId);
				ct.setOperatorId(opsId);

				tracker.track(ct);

				return true;
			} else {
				log.error(logMsg + " insert failed");
			}

		} catch (Exception e) {
			log.error("Exception occured while adding miscelleneous packets", e);

		}
		return false;

	}

}
