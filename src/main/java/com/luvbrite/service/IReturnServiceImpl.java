package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.repository.IPacketRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IReturnServiceImpl  implements IReturnsService {

	@Autowired
	private  Tracker tracker;

	@Autowired
	private IPacketRepository IpacketRepo;


	@Override
	public int addReturn(String packetCode, String reason, Integer shopId, Integer operatorId) throws Exception {
		// TODO Auto-generated method stub

		int returnStatus = IpacketRepo.returnPacket(packetCode, reason, shopId);

		return returnStatus;
	}

}
