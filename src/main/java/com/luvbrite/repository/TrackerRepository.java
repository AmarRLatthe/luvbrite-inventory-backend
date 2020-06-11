package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.luvbrite.model.ChangeTrackerDTO;
import com.luvbrite.model.ChangeTrackerExtDTO;
import com.luvbrite.model.PaginatedTrackingLogs;
import com.luvbrite.model.Pagination;
import com.luvbrite.model.PaginationLogic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TrackerRepository implements ITrackRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	private final int itemsPerPage = 15;
	private Pagination pg;

	@Override
	public boolean UpdateTracker(ChangeTrackerDTO ct) {

		StringBuffer trackerUpdateQry = new StringBuffer();
		trackerUpdateQry.append(
				"INSERT INTO change_tracker(operator_id, item_id, action_type, action_details, action_on,shop_id) VALUES (?, ?, ?, ?, ?,?);");

		boolean status = false;
		int updateStatus = 0;

		try {
			updateStatus = jdbcTemplate.update(trackerUpdateQry.toString(), new Object[] { ct.getOperatorId(),
					ct.getItemId(), ct.getActionType(), ct.getActionDetails(), ct.getActionOn(), ct.getShopId() });

			if (updateStatus == 0) {
				log.error("ChangeTracker insert failed. Q - ");
				status = false;
			} else {
				status = true;
				log.info("Tracker updated successfully");
			}

		} catch (Exception e) {
			log.error("Exception occured while tracking  ", e);
			status = true;
		}
		return status;

	}

	@Override
	public PaginatedTrackingLogs listTracks(String sort, String sortDirection, String obj, Integer currentPage,
			Integer operatorId,	Integer shopId) throws Exception{

		String  qWHERE = "", qOFFSET = "", qLIMIT = " LIMIT " + itemsPerPage + " ",
				qORDERBY = "ORDER BY ct.date_time DESC";

		PaginationLogic pgl = null;
		int offset = 0;

		if (operatorId != 0) {
			qWHERE = "WHERE ud.id = " + operatorId + " ";
		}

		if ((obj != null) && !obj.trim().equals("")) {
			if (qWHERE.equals("")) {
				qWHERE = "WHERE ct.action_on = $$" + obj + "$$ ";

			} else {
				qWHERE += " AND ct.action_on = $$" + obj + "$$ ";
			}
		}

		if ((sort != null) && !sort.trim().equals("")) {
			switch (sort) {
			case "actionby":
				qORDERBY = "ORDER BY ct.operator_id " + sortDirection + " ";
				break;

			case "actionon":
				qORDERBY = "ORDER BY ct.action_on " + sortDirection + " ";
				break;

			case "date":
				qORDERBY = "ORDER BY ct.date_time " + sortDirection + " ";
				break;
			}
		}

		if (currentPage <= 0) {
			currentPage = 1;
		}

		StringBuffer countStringBuffer = new StringBuffer();
		countStringBuffer
		.append("SELECT COUNT(*) ")
		.append("FROM change_tracker ct  ")
		.append("LEFT JOIN user_details ud ON ud.id = ct.operator_id  ")
		.append(qWHERE)
		.append("AND ct.shop_id")
		.append("=")
		.append(shopId);

		System.out.println("ListTrackerLog - " + countStringBuffer);
		Integer totalLogs = jdbcTemplate.queryForObject(countStringBuffer.toString(), Integer.class);


		if (totalLogs > 0L) {
			pgl = new PaginationLogic(totalLogs, itemsPerPage, currentPage);
		}

		pg = pgl.getPg();
		offset = pg.getOffset();
		if (offset > 0) {
			qOFFSET = " OFFSET " + offset;
		}


		StringBuffer queryStringBuffer = new StringBuffer();

		queryStringBuffer
		.append("SELECT ct.*,")
		.append("TO_CHAR(ct.date_time, 'MM/dd/yyyy HH:MI AM') AS date_string, ")
		.append("ud.username ")
		.append("FROM change_tracker ct ")
		.append("LEFT JOIN user_details ud ON ud.id = ct.operator_id ")
		.append(qWHERE)
		.append("AND ")
		.append("ct.shop_id")
		.append("= ")
		.append(shopId)
		.append(qORDERBY)
		.append(qLIMIT)
		.append(qOFFSET);

		System.out.println("queryStringBuffer ::::"+queryStringBuffer);

		List<ChangeTrackerExtDTO> logs = new ArrayList<ChangeTrackerExtDTO>();

		logs = jdbcTemplate.query(queryStringBuffer.toString(),
				new RowMapper<ChangeTrackerExtDTO>() {

			@Override
			public ChangeTrackerExtDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				ChangeTrackerExtDTO log = new ChangeTrackerExtDTO();

				log.setId(rs.getInt("id"));
				log.setActionType(rs.getString("action_type"));
				log.setActionDetails(rs.getString("action_details"));
				log.setDate(rs.getString("date_string"));
				log.setItemId(rs.getInt("item_id"));

				String actionOn = rs.getString("action_on");
				switch (actionOn) {

				case "cat":
					log.setActionOn("Category");
					break;
				case "ops":
					log.setActionOn("Operator");
					break;
				case "prod":
					log.setActionOn("Product");
					break;
				default:
					log.setActionOn(actionOn);
					break;
				}

				int opsId = rs.getInt("operator_id");
				log.setOperatorId(opsId);
				log.setOperatorName(rs.getString("username"));

				return log;
			}
		});

		return new PaginatedTrackingLogs(pg,logs);
	}
}
