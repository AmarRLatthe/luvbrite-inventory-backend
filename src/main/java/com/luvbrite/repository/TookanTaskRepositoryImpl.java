package com.luvbrite.repository;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.luvbrite.jdbcutils.DeliveryPacketDTOMapper;
import com.luvbrite.jdbcutils.JobInsertRowMapper;
import com.luvbrite.jdbcutils.TaskRequestDTOMapper;
import com.luvbrite.jdbcutils.model.JobInsertDetail;
import com.luvbrite.model.DeliveredPacketDTO;
import com.luvbrite.model.SoldPacketsDTO;
import com.luvbrite.model.tookan.TaskRequest;
import com.luvbrite.model.tookan.TookanResponse;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class TookanTaskRepositoryImpl implements ITookanTaskRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public boolean deleteAlreadyCreatedTask(int salesID, int shopId) throws SQLException {
		if (salesID == 0) {
			log.info("Invalid salesID " + salesID);
			return false;
		}

		boolean ifTaskExists = false;

		StringBuffer deleteTaskForSalesIDQuery = new StringBuffer();

		deleteTaskForSalesIDQuery.append("DELETE FROM job_details").append("WHERE ").append("sales_id = ")
		.append(salesID).append("AND").append(shopId);

		int rowsDeleted = jdbcTemplate.update(deleteTaskForSalesIDQuery.toString(), new Object[] { salesID, shopId });

		if (rowsDeleted == 0) {
			log.info("Rows deleted for sales_id " + salesID + " and shopId = " + shopId + " is 0");
			ifTaskExists = false;
		} else {
			log.info("Number Of rows  deleted are " + rowsDeleted);
			ifTaskExists = true;
		}

		return ifTaskExists;

	}

	@Override
	public JobInsertDetail saveJobDetails(TookanResponse response, int salesId, int shopId) throws Exception {

		StringBuffer updateJobDetails = new StringBuffer();
		updateJobDetails
		.append("INSERT INTO job_details")
		.append("(job_id,sales_id,order_id,message,status,tracking_link,customer_name,create_task_response,created_at,is_cancelled,shop_id)")
		.append("values(?,?,?,?,?,?,?,?,to_timestamp(?,'YYYY-MM-DD HH:MI:SS'),?,?)")
		.append("RETURNING job_id,to_char(created_at,'YYYY-MM-DD HH:MI:SS'); ");

		String tookanTaskCreatedResponse = new Gson().toJson(response);

		JobInsertDetail jobInsertedDetail = (JobInsertDetail) jdbcTemplate.query(updateJobDetails.toString(),
				new Object[] { Long.parseLong(response.getData().getJob_id()), salesId,
						Integer.parseInt(response.getData().getOrder_id()), response.getMessage(),
						Integer.parseInt(response.getStatus()), response.getData().getTracking_link(),
						response.getData().getCustomer_name(), tookanTaskCreatedResponse, currentDateTime(), 0,
						shopId },
				new JobInsertRowMapper());
		if (jobInsertedDetail == null) {
			log.error("JobDetails could not be inserted ");
			return null;
		}

		return jobInsertedDetail;
	}

	private static String currentDateTime() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return ft.format(dNow);
	}

	@Override
	public boolean cancelJobCreated(int salesId, int shopId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Long isJobAlreadyExists(int salesId, int shopId) {

		StringBuffer fetchJobForProvidedSalesIdOfAShop = new StringBuffer();

		fetchJobForProvidedSalesIdOfAShop.append("SELECT job_id FROM job_details WHERE sales_id = ").append(salesId)
		.append(" AND shop_id = ").append(shopId);

		Long jobId = jdbcTemplate.queryForObject(fetchJobForProvidedSalesIdOfAShop.toString(), Long.class);

		if (jobId == 0) {
			log.info("Job does not exists for salesId {} for shopId {} " + salesId + " " + " " + shopId);
			return jobId;
		}

		return jobId;

	}

	@Override
	public boolean updateJobCancelStatus(TookanResponse jobCancelResponse, Long jobId, int shopId) {

		if ((jobCancelResponse == null) || (jobId == 0L)||(shopId==0)) {
			if (jobCancelResponse == null) {
				log.info("Cannot update because tookan response for cancelTask is null");
			} else {
				log.info("Cannot update because job_id or shopId can not be 0");
			}
			return false;
		}

		int isCancelled = 0;
		boolean isUpdated = false;
		int rowsUpdated = 0;
		String responseMessage = jobCancelResponse.getStatus().toLowerCase().trim();

		StringBuffer updateJobCancelStatus = new StringBuffer();

		updateJobCancelStatus.append(
				"UPDATE job_details SET is_cancelled = ? , cancellation_date = to_timestamp(?,'YYYY-MM-DD HH:MI:SS')")
		.append("WHERE job_id = ?").append("AND shop_id = ").append(shopId);

		if (responseMessage.equalsIgnoreCase("200")) {

			rowsUpdated = jdbcTemplate.update(updateJobCancelStatus.toString(),
					new Object[] { 1, currentDateTime(), jobId, shopId });

			isCancelled = 1;
		} else {

			rowsUpdated = jdbcTemplate.update(updateJobCancelStatus.toString(),
					new Object[] { -1, currentDateTime(), jobId, shopId });

			isCancelled = -1;
		}

		if (rowsUpdated == 0) {
			log.info("No rows updated for jobCancellation" + jobId + " for shopId " + shopId);
			isUpdated = false;
		} else {
			log.info("is_cancelled status updated to " + isCancelled + " for job_id " + jobId);
			isUpdated = true;
		}

		return isUpdated;
	}

	@Override
	public TaskRequest createTaskRequest(int salesId,int shopId) throws Exception {
		StringBuffer taskDetailsQuery = new StringBuffer();

		taskDetailsQuery
		.append("SELECT ds.*, ooi.*, TO_CHAR(ds.date_called, 'MM/dd/yyyy HH:MI AM') AS formatted_date_called, ")
		.append("TO_CHAR(ds.date_arrived, 'MM/dd/yyyy HH:MI AM') AS formatted_date_arrived, ")
		.append("TO_CHAR(ds.date_finished, 'MM/dd/yyyy HH:MI AM') AS formatted_date_finished ")
		.append("FROM dispatch_sales_info ds ")
		.append("LEFT JOIN online_order_info ooi ON ooi.dispatch_sales_id = ds.id ")
		.append("WHERE ds.id = ")
		.append(salesId )
		.append("AND ")
		.append("ds.shop_id = ")
		.append(shopId);




		TaskRequest tooakanRequest = (TaskRequest) jdbcTemplate.query(taskDetailsQuery.toString(),
				new TaskRequestDTOMapper());
		return tooakanRequest;
	}



	@Override
	public List<DeliveredPacketDTO> deliveredPackets(List<SoldPacketsDTO> sps, int salesid,int shopId){
		StringBuffer sql_dispatchedProductInfo = new StringBuffer();


		sql_dispatchedProductInfo.append("select prods.product_name,COUNT(prods.product_name) as items,SUM(pi.selling_price) as selling_price ")
		.append(" from packet_inventory pi ")
		.append("JOIN  purchase_inventory pur_i ON pi.purchase_id = pur_i.id ")
		.append("JOIN  products prods ON prods.id = pur_i.product_id ")
		.append("JOIN dispatch_sales_info dsi ON dsi.id = pi.sales_id ")
		.append("WHERE sales_id = ")
		.append(salesid)
		.append("AND pi.shop_id = ")
		.append(shopId)
		.append(" group by prods.product_name;");

		List<DeliveredPacketDTO> deliveredPackets =	jdbcTemplate.query(sql_dispatchedProductInfo.toString(), new DeliveryPacketDTOMapper());


		return deliveredPackets;
	}



}
