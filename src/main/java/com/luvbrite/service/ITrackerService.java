package com.luvbrite.service;

import com.luvbrite.model.ChangeTrackerDTO;
import com.luvbrite.model.PaginatedTrackingLogs;

public interface ITrackerService {

	boolean track(ChangeTrackerDTO ct);

	public PaginatedTrackingLogs listTracks(String orderBy, String sortDirection,String obj, Integer cpage, Integer operatorId,
			Integer shopId) throws Exception;

}
