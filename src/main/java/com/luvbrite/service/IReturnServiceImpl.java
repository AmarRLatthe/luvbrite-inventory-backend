package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.PaginatedReturns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.repository.IPacketRepository;
import com.luvbrite.repository.IReturnsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IReturnServiceImpl  implements IReturnsService {



	@Autowired
	private IReturnsRepository  iReturnsRepoImpl;



	@Autowired
	private IPacketRepository  iPacketRepo;

	@Override
	public int addReturn(String packetCode, String reason, Integer shopId, Integer operatorId) throws Exception {
		// TODO Auto-generated method stub

		int returnStatus = iPacketRepo.returnPacket(packetCode, reason, shopId);

		return returnStatus;
	}



	@Override
	public List<String> listReasons(Integer shopId){
		return	iReturnsRepoImpl.listReturnReasons(shopId);

	}

	@Override
	public PaginatedReturns listReturns(Integer currentPage,Integer shopId) throws  Exception {
		return iReturnsRepoImpl.listReturns(currentPage,shopId);
	}

	@Override
	public int deleteReturn(Integer returnId, Integer shopId,Integer operatorId) throws Exception {
		return iReturnsRepoImpl.deleteReturn(returnId,shopId,operatorId);
	}

}
