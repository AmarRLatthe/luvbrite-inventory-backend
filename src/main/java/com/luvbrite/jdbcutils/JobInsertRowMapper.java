package com.luvbrite.jdbcUtils;

import java.sql.ResultSet; 
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luvbrite.jdbcUtils.model.JobInsertDetail;

public class JobInsertRowMapper implements RowMapper<JobInsertDetail> {

	@Override
	public JobInsertDetail mapRow(ResultSet rs, int rowNum) throws SQLException {

		JobInsertDetail jobInserted = new JobInsertDetail();
		jobInserted.setJobId(rs.getInt("job_id"));
		jobInserted.setCreatedAt(rs.getString("created_at"));

		return jobInserted;
	}
}
