package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luvbrite.jdbcutils.model.JobInsertDetail;
import com.luvbrite.model.ChangeTrackerDTO;
import com.luvbrite.model.tookan.TookanResponse;
import com.luvbrite.repository.ITookanTaskRepository;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author dell
 */
@Service
@Slf4j
public class SaveJobDetailsServiceImpl implements ISaveJobDetailsService {

	@Autowired
	Tracker track;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	ITookanTaskRepository tookanTaskRepoImpl;

	@Override
	public boolean saveJobDetails(TookanResponse taskResponse, int salesId, int shopId, int operatorId)
			throws Exception {

		JobInsertDetail jobInsertDetail = null;



		if (isJobCreatedSuccessfully(taskResponse, salesId, shopId)) {
			log.info("Task is  created successfully for salesID : " + salesId);
			tookanTaskRepoImpl.deleteAlreadyCreatedTask(salesId, shopId);
			jobInsertDetail = tookanTaskRepoImpl.saveJobDetails(taskResponse, salesId,shopId);

		}

		if (jobInsertDetail == null) {
			log.error("task could not be saved for shop {} , sales {} , operator{} " + shopId + " " + " " + salesId
					+ " " + operatorId);

			return false;
		}

		ChangeTrackerDTO ct = new ChangeTrackerDTO();
		ct.setActionDetails("Task Created");
		ct.setActionType("insert");
		ct.setActionOn("Tookan");
		ct.setOperatorId(operatorId);
		ct.setItemId(jobInsertDetail.getJobId()); //
		ct.setDate(jobInsertDetail.getCreatedAt()); // Set Current Date new UpdateTracker(ct, con);

		track.track(ct);

		return true;

	}

	private boolean isJobCreatedSuccessfully(TookanResponse createTaskResponse, int salesId, int shopId) {
		boolean isJobCreationSuccessful = false;

		if (!createTaskResponse.getStatus().equals("200")) {
			log.error("Task could not be created because :- " + createTaskResponse.getMessage() + " for salesID :- "
					+ salesId + "for shop :" + shopId);
			isJobCreationSuccessful = false;

		} else {
			isJobCreationSuccessful = true;
		}

		return isJobCreationSuccessful;
	}



}
