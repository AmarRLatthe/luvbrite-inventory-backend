package com.luvbrite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.PurchaseDTO;
import com.luvbrite.repository.IDriverRepository;
import com.luvbrite.repository.IPurchaseRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PurchaseServiceImpl implements IPurchaseService{

	
	@Autowired
	private IPurchaseRepository iPurchaseRepository;
	
	
	@Override
	public PurchaseDTO addPurchase(PurchaseDTO purchase) throws Exception {
		return	iPurchaseRepository.addPurchase(purchase);
	}


	@Override
	public List<PurchaseDTO> getAllPurchases() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
