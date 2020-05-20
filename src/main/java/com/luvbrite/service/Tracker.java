package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.controller.ChangeTrackerDTO;
import com.luvbrite.repository.TrackerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Tracker {

	@Autowired
	TrackerRepository Tracker;

	public boolean track(ChangeTrackerDTO ct) {
		return Tracker.UpdateTracker(ct);
	}

}
