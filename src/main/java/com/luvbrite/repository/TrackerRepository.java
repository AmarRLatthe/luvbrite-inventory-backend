package com.luvbrite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.luvbrite.controller.ChangeTrackerDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TrackerRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public boolean UpdateTracker(ChangeTrackerDTO ct) {

		StringBuffer trackerUpdateQry = new StringBuffer();
		trackerUpdateQry.append(
				"INSERT INTO change_tracker(operator_id, item_id, action_type, action_details, action_on,shop_id) VALUES (?, ?, ?, ?, ?);");

		boolean status = false;
		int updateStatus = 0;

		try {
			updateStatus = jdbcTemplate.update(trackerUpdateQry.toString(),
					new Object[] {
							ct.getOperatorId(),
							ct.getItemId(),
							ct.getActionType(),
							ct.getActionDetails(),
							ct.getActionOn(),
							ct.getShopId()
			});

			if (updateStatus == 0) {
				log.error("ChangeTracker insert failed. Q - ");
				status = false;
			} else {
				status = true;
				log.info("Tracker updated successfully");
			}

		} catch (Exception e) {
			log.error("Exception occured while tracking  ",e);
			status = true;
		}
		return status;

	}


}
