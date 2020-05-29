package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.SinglePacketDTO;
import com.luvbrite.repository.IPacketRepository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class PacketServiceImpl implements IPacketService{

	
	@Autowired
	private IPacketRepository iPacketRepository;

	@Override
	public int createSinglePacket(SinglePacketDTO singlePacket) {
		try {
			return -1;
		} catch (Exception e) {
			return -1;				
		}
	}
}
