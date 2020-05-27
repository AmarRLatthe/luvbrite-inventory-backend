package com.luvbrite.repository;

import java.sql.SQLException;
import java.util.List;

import com.luvbrite.jdbcutils.model.JobInsertDetail;
import com.luvbrite.model.DeliveredPacketDTO;
import com.luvbrite.model.SoldPacketsDTO;
import com.luvbrite.model.tookan.TaskRequest;
import com.luvbrite.model.tookan.TookanResponse;

public interface ITookanTaskRepository {



	public boolean deleteAlreadyCreatedTask(int salesID, int shopId) throws SQLException;

	public JobInsertDetail saveJobDetails(TookanResponse response,int salesId,int shopId) throws Exception;

	public boolean cancelJobCreated(int salesId,int shopId);

	public Long  isJobAlreadyExists(int salesId ,int shopId);

	public boolean updateJobCancelStatus(TookanResponse jobCancelResponse, Long job_id,int shopId);

	public TaskRequest createTaskRequest(int salesId,int shopId) throws Exception;

	public List<DeliveredPacketDTO> deliveredPackets(List<SoldPacketsDTO> sps, int salesid,int shopId);

	public double getTotalTax(int salesId, int shopId);
}
