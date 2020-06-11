package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.ChangeTrackerDTO;
import com.luvbrite.model.PaginatedTrackingLogs;
import com.luvbrite.repository.ITrackRepository;

@Service
public class Tracker implements ITrackerService {

	@Autowired
	private ITrackRepository Tracker;

	@Override
	public boolean track(ChangeTrackerDTO ct) {
		return Tracker.UpdateTracker(ct);
	}




	@Override
	public PaginatedTrackingLogs listTracks(String orderBy, String sortDirection,String obj, Integer cpage, Integer operatorId,
			Integer shopId) throws Exception {
		return Tracker.listTracks(orderBy, sortDirection, obj, cpage, operatorId, shopId);
	}}
