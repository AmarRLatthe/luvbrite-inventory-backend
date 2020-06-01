package com.luvbrite.service;

import com.luvbrite.model.PaginatedPackets;
import com.luvbrite.model.SinglePacketDTO;

public interface IPacketService {

	int createSinglePacket(SinglePacketDTO singlePacket);

	public PaginatedPackets listPackets(Integer purchaseId, Integer salesId, Integer shopId, Boolean notSold,
			Boolean sold, Boolean allPackets, String orderBy, String sortDirection, String packetCode, String allmisc,
			Integer currentPage) throws Exception;
}
