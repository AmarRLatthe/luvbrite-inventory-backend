package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luvbrite.model.PaginatedPackets;
import com.luvbrite.model.BulkPacketsCreation;
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
	@Autowired
	private Tracker tracker; 
	
	@Override
	public int createSinglePacket(SinglePacketDTO singlePacket) {
		try {
			
			return iPacketRepository.createSinglePkt(singlePacket);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;				
		}
	}

	@Override
	public PaginatedPackets listPackets(Integer purchaseId, Integer salesId, Integer shopId, Boolean notSold,
			Boolean sold, Boolean allPackets, String orderBy, String sortDirection, String packetCode, String allmisc,
			Integer currentPage) throws Exception{

		return	iPacketRepository.listPackets(purchaseId,
				salesId,
				shopId,
				notSold,
				sold,
				allPackets,
				orderBy,
				sortDirection,
				packetCode,
				allmisc,
				currentPage) ;
	}









	public int updatePktById(Integer id, SinglePacketDTO singlePacket) {
		try {
			return iPacketRepository.updatePktById(id,singlePacket);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}

	}

	@Transactional
	@Override
	public int createBulkPacket(BulkPacketsCreation packets) {
		try {
//			int createBulk = iPacketRepository.createBulkPackets(packets);
//			if(createBulk>0) {
//				ChangeTrackerDTO ct = new ChangeTrackerDTO();
//				ct.setShopId(packets.getShopId());
//				ct.setOperatorId(packets.getOperatorId());
//				ct.setItemId(packets.get);
//			}
			return iPacketRepository.createBulkPackets(packets);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public int updatePktsByPriceNWeightNPurchaseId(Double price, Double weight, Integer purchaseId) {
		try {
			return iPacketRepository.updatePktsByPriceNWeightNPurchaseId(price,weight,purchaseId);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public boolean isAvailPacketBySKU(String sku) {
		try {
			return iPacketRepository.isAvailPacketBySKU(sku); 
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return false;
		}

	}

	@Override
	public int deletePktById(Integer id) {
		try {
			return iPacketRepository.deletePktById(id);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
		
	}
}
