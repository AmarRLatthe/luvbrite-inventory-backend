package com.luvbrite.model.tookan;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class TookanActivityTimeLineResponse {
	private String message;
	private int status;

	private List<TookanActivityTimelineResponseData> data;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<TookanActivityTimelineResponseData> getData() {
		return data;
	}

	public void setData(List<TookanActivityTimelineResponseData> data) {
		this.data = data;
	}

}
