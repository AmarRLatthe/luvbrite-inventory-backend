package com.luvbrite.repository;

import com.luvbrite.model.ChangeTrackerDTO;
import com.luvbrite.model.PaginatedTrackingLogs;

public interface ITrackRepository {

	boolean UpdateTracker(ChangeTrackerDTO ct);

	public PaginatedTrackingLogs listTracks(String sort, String sortDirection, String obj, Integer currentPage,
			Integer operatorId,	Integer shopId) throws Exception;
}
