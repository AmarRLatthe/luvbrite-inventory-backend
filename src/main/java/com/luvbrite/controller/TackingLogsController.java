package com.luvbrite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.PaginatedTrackingLogs;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.ITrackerService;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("api/track/")
public class TackingLogsController {

	@Autowired
	private ITrackerService trackerServiceImpl;

	@Autowired
	private IUserService iUserService;

	@GetMapping("/listtracker")
	public ResponseEntity<CommonResponse> listTrack(
			@RequestParam(value = "driverId", required = false) Integer driverId,
			@RequestParam(value = "sort", required = false) String orderBy,
			@RequestParam(value = "sdir", required = false) String sortDirection,
			@RequestParam(value = "cpage", required = false) Integer currentPage,
			@RequestParam(value = "obj", required = false) String obj, Authentication authentication) {

		// cpage=1&op=0&dr=0&obj=dispatch&sort=&sdir=ASC
		driverId = driverId == null ? 0 : driverId;
		orderBy = orderBy == null ? "" : orderBy;
		sortDirection = sortDirection == null ? "ASC" : sortDirection;
		currentPage = currentPage == null ? 0 : currentPage;

		CommonResponse response = new CommonResponse();

		UserDetails userDetails = iUserService.getByUsername(authentication.getName());
		try {
			if (userDetails != null) {

				Integer shopId = userDetails.getShopId();
				Integer operatorId = userDetails.getId();
				PaginatedTrackingLogs paginatedTrackingLogs = null;

				paginatedTrackingLogs = trackerServiceImpl.listTracks(orderBy, sortDirection, obj, currentPage,
						operatorId, shopId);

				if (paginatedTrackingLogs != null) {

					if (paginatedTrackingLogs.getTrackingLogs().size() > 0) {
						response.setCode(200);
						response.setData(paginatedTrackingLogs);
						response.setMessage("Tracking logs retrieved successfully");
						response.setStatus("SUCCESS");
					} else {
						response.setCode(204);
						response.setData(paginatedTrackingLogs);
						response.setMessage("No tracking records for this user");
						response.setStatus("SUCCESS");
					}
				} else {
					response.setCode(500);
					response.setData(paginatedTrackingLogs);
					response.setMessage("SERVER ERROR");
					response.setStatus("FAILURE");
				}

				return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);

			} else {

				response.setCode(401);
				response.setMessage("Could not found user in database");
				response.setStatus("FAILURE");

				return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
			}

		} catch (Exception e) {
			log.error("Exception occured while fetching tracking logs ", e);

			response.setCode(500);
			response.setMessage("SERVER ERROR");
			response.setStatus("FAILURE");

			return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);

		}

	}

}
