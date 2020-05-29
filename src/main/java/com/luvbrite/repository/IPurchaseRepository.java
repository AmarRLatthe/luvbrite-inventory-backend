package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.PurchaseDTO;
import com.luvbrite.model.PurchasedOrderDTO;

public interface IPurchaseRepository  {

	PurchaseDTO addPurchase(PurchaseDTO purchase) throws Exception;

	List<PurchaseDTO> getAllPurchases() throws Exception;

	int updatePurchaseById(Integer id, PurchaseDTO purchase);

	public PurchasedOrderDTO getPurchasedOrderDetailsById(Integer id);



}
