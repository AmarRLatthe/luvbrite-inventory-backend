package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.PacketExtDTO;
import com.luvbrite.model.SinglePacketDTO;

public interface IPacketService {

	int createSinglePacket(SinglePacketDTO singlePacket);

	public List<PacketExtDTO> listPackets(Integer purchaseId, Integer salesId, Integer shopId, Boolean notSold,
			Boolean sold, Boolean allPackets, String orderBy, String sortDirection, String packetCode, String allmisc,
			Integer currentPage) throws Exception;
}
