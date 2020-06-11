package com.luvbrite.model.tookan;

import java.util.List;

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
