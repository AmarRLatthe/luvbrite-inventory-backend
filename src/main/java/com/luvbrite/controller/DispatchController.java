package com.luvbrite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonResponse.CommonResponse;
import com.luvbrite.model.Pagination;

@RestController
public class DispatchController {

	private Pagination pg;
	private final int itemsPerPage = 15;

	@GetMapping("/api/listdispatches")
	public ResponseEntity<CommonResponse> listdispatches(@RequestParam(value="d",required=true) Integer driverId,
			@RequestParam(value="id",required = true) int dispatchId,
			@RequestParam(value="ca",required = false) int cancelled,
			@RequestParam(value="fn",required = false) Boolean finished,
			@RequestParam(value="nf",required = false) Boolean notFinished,
			@RequestParam(value="q",required = true) String q,
			@RequestParam(value="sort",required = false) String orderBy,
			@RequestParam(value="mode",required = false) String mode,
			@RequestParam(value="sdir",required = false) String qSORTDIR,
			@RequestParam(value="cpage",required= true)int currentPage){




		String qWHERE = "",
				qOFFSET = "",
				qLIMIT = " LIMIT " + itemsPerPage + " ",
				qORDERBY = " ORDER by ds.id ";
		qSORTDIR = " ASC";







		return null;
	}

}
