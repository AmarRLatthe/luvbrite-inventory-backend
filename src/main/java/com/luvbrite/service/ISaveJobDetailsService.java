package com.luvbrite.service;

import com.luvbrite.model.tookan.TookanResponse;

public interface ISaveJobDetailsService {


	public boolean saveJobDetails(TookanResponse taskResponse, int salesId, int shopId, int operatorId)
			throws Exception;
}
