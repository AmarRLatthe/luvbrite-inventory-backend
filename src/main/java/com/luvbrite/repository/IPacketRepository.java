package com.luvbrite.repository;

import com.luvbrite.model.BulkPacketsCreation;
import com.luvbrite.model.PaginatedPackets;
import com.luvbrite.model.SinglePacketDTO;

public interface IPacketRepository {

	int createSinglePkt(SinglePacketDTO singlePacket);

	int updatePktById(Integer id, SinglePacketDTO singlePacket);

	int createBulkPackets(BulkPacketsCreation packets);

	int updatePktsByPriceNWeightNPurchaseId( Double price, Double weight, Integer purchaseId);

	boolean isAvailPacketBySKU(String sku);

	public int deletePktById(Integer id);

	public PaginatedPackets listPackets(Integer purchaseId, Integer salesId, Integer shopId, Boolean notSold,
			Boolean sold, Boolean allPackets, String orderBy, String sortDirection, String packetCode, String allmisc,
			Integer currentPage) throws Exception;



	public int updateSaleDateNSalesIdForPacketCode(String packetCode,String saleDate,int saleId,int shopId);

	public int updateAmountNSaleDateNSaleIdForPacketCode(double amount ,String packetCode,String saleDate,int saleId,int shopId);




	public boolean checkIfValidBarcode(String packetCode, Integer shopId);


	public int returnPacket(String packetCode , String reason, Integer shopId) throws Exception;


	long getPacketIdIfNotReturned(String packetCode, Integer shopId) throws Exception;











}
