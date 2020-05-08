package com.luvbrite.service;

import java.util.List;

import com.luvbrite.model.PurchaseDTO;

public interface IPurchaseService {

	
	PurchaseDTO addPurchase(PurchaseDTO purchase) throws Exception;
	
    List<PurchaseDTO> getAllPurchases() throws Exception;

}
