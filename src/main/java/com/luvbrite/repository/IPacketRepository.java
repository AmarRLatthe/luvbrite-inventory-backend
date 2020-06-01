package com.luvbrite.repository;

import com.luvbrite.model.PaginatedPackets;

public interface IPacketRepository {


	public PaginatedPackets listPackets(Integer purchaseId, Integer salesId, Integer shopId, Boolean notSold,
			Boolean sold, Boolean allPackets, String orderBy, String sortDirection, String packetCode, String allmisc,
			Integer currentPage) throws Exception;
}
