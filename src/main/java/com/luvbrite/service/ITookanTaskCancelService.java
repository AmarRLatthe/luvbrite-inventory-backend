package com.luvbrite.service;

import org.springframework.web.client.RestTemplate;

import com.luvbrite.model.tookan.TookanResponse;

public interface ITookanTaskCancelService {

	public boolean cancelJobCreated(int salesId, int shopId, int operatorId);
	public TookanResponse postCancelJobRequest(long jobId, RestTemplate restTemplate) ;

}
