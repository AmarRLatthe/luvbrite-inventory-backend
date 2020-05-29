package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.PacketExtDTO;

public interface IPacketRepository {


	public List<PacketExtDTO> listPackets(Integer purchaseId, Integer salesId, Integer shopId, Boolean notSold,
			Boolean sold, Boolean allPackets, String orderBy, String sortDirection, String packetCode, String allmisc,
			Integer currentPage) throws Exception;
}
