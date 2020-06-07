package com.luvbrite.model;

import java.util.List;

public class PaginatedDispatch {
	public PaginatedDispatch(Pagination pg, List<DispatchSalesExt> dispatches) {
		this.pg = pg;
		this.dispatches = dispatches;
	}

	public List<DispatchSalesExt> getDispatches() {
		return dispatches;
	}

	public void setDispatches(List<DispatchSalesExt> dispatches) {
		this.dispatches = dispatches;
	}

	private Pagination pg;
	private List<DispatchSalesExt> dispatches;

	public Pagination getPg() {
		return pg;
	}

	public void setPg(Pagination pg) {
		this.pg = pg;
	}


}


