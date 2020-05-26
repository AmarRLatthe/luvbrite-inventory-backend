package com.luvbrite.jdbcutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.model.tookan.TaskRequest;

public class TaskRequestDTOMapper implements RowMapper<TaskRequest> {

	@Override
	public TaskRequest mapRow(ResultSet rs, int rowNum) throws SQLException {

		TaskRequest taskRequest = new TaskRequest();

		taskRequest.setOrder_id(rs.getString("order_number"));
		taskRequest.setJob_description("Luvbrite Order Delivery");
		taskRequest.setCustomer_email(rs.getString("email"));
		taskRequest.setCustomer_username(rs.getString("client_name"));
		taskRequest.setCustomer_phone(rs.getString("phone"));
		taskRequest.setCustomer_address(rs.getString("address"));
		taskRequest.setLatitude(rs.getString("lat"));
		taskRequest.setLongitude(rs.getString("lng"));
		taskRequest.setJob_description("delivering cannabis");
		taskRequest.setJob_delivery_datetime(rs.getString("date_finished"));
		taskRequest.setCustom_field_template("Cannabis_Delivery");
		taskRequest.setTeam_id("");
		taskRequest.setAuto_assignment("0");
		taskRequest.setHas_pickup("0");
		taskRequest.setHas_delivery("1");
		taskRequest.setLayout_type("0");
		taskRequest.setTracking_link(1);
		taskRequest.setTimezone("+480");
		taskRequest.setFleet_id("");
		taskRequest.setNotify(1);
		taskRequest.setTags("");
		taskRequest.setGeofence(0);

		return taskRequest;
	}

}
