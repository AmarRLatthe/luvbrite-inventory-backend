package com.luvbrite.model;

import java.util.List;

public class PaginatedTrackingLogs {

	public PaginatedTrackingLogs(Pagination pg,List<ChangeTrackerExtDTO> trackingLogs){
		this.pg= pg;
		this.trackingLogs=trackingLogs;
	}

	private Pagination pg;

	private List<ChangeTrackerExtDTO> trackingLogs;

	public Pagination getPg() {
		return pg;
	}

	public void setPg(Pagination pg) {
		this.pg = pg;
	}

	public List<ChangeTrackerExtDTO> getTrackingLogs() {
		return trackingLogs;
	}

	public void setTrackingLogs(List<ChangeTrackerExtDTO> trackingLogs) {
		this.trackingLogs = trackingLogs;
	}






}
