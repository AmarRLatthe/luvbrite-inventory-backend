package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luvbrite.constants.APIs;
import com.luvbrite.model.tookan.CancelJobCreatedRequest;
import com.luvbrite.model.tookan.TookanResponse;
import com.luvbrite.repository.ITookanTaskRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TookanTaskCancelServiceImpl  implements ITookanTaskCancelService{

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ITookanTaskRepository tookanTaskRepo;


	private static final String TOOKAN_API_KEY = APIs.TOOKAN_API_KEY;


	@Override
	public boolean cancelJobCreated(int salesId, int shopId, int operatorId) {

		boolean isJobCancelled = false;
		if (salesId == 0) {
			log.info("0 is not a valid salesID");
			return false;
		}

		Long jobId = 0l;

		jobId = tookanTaskRepo.isJobAlreadyExists(salesId, shopId);

		if (jobId > 0) {
			TookanResponse tookanResponse = postCancelJobRequest(jobId, restTemplate);
			isJobCancelled = tookanTaskRepo.updateJobCancelStatus(tookanResponse, jobId, shopId);

			return isJobCancelled;
		}

		return isJobCancelled;

	}





	@Override
	public TookanResponse postCancelJobRequest(long jobId, RestTemplate restTemplate) {
		String cancelJoburl = APIs.TOOKAN_CANCEL_TASK;

		CancelJobCreatedRequest cancelJob = new CancelJobCreatedRequest();
		cancelJob.setApi_key(TOOKAN_API_KEY);
		cancelJob.setJob_id(String.valueOf(jobId));
		String cancelJobResponse = "";
		TookanResponse tookanResponse = null;

		try {

			ObjectMapper mapper = new ObjectMapper();
			String cancelJobRequest = mapper.writeValueAsString(cancelJob);
			log.info("CancelJobRequest:-" + cancelJobRequest);

			tookanResponse = restTemplate.postForObject(cancelJoburl, cancelJobRequest, TookanResponse.class);

			cancelJobResponse = mapper.writeValueAsString(tookanResponse);
			log.info("job cancel response from :-" + cancelJobResponse);

		} catch (JsonProcessingException ex) {
			log.error("JsonProcessException while processing cancelJobRequest object", ex);
			return null;
		} catch (Exception ex) {
			log.error("Exception occured while posting cancel job response", ex);
			return null;
		}
		return tookanResponse;
	}




}
