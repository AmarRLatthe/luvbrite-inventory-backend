package com.luvbrite.model;

import java.util.List;

public class PaginatedPurchase {
	public PaginatedPurchase(Pagination pg, List<PurchaseDTO> purchases) {
		this.pg = pg;
		this.purchases = purchases;
	}

	private Pagination pg;
	private List<PurchaseDTO> purchases;

	public Pagination getPg() {
		return pg;
	}

	public void setPg(Pagination pg) {
		this.pg = pg;
	}

	public List<PurchaseDTO> getPurchases() {
		return purchases;
	}

	public void setPurchases(List<PurchaseDTO> purchases) {
		this.purchases = purchases;
	}

}
