package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.repository.IPacketRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddPacketsAfterSales {

	@Autowired
	Tracker track;

	@Autowired
	IPacketRepository packetRepositoryImpl;

	public int addPackets(String packetCode, String saleDate, double amount, int opsId, int salesId, int shopId) {

		int packetId = 0;
		if (amount == 0) {
			packetId = packetRepositoryImpl.updateSaleDateNSalesIdForPacketCode(packetCode, saleDate, salesId, shopId);
		} else {
			packetId = packetRepositoryImpl.updateAmountNSaleDateNSaleIdForPacketCode(amount, packetCode, saleDate,
					salesId, shopId);
		}



		return packetId;

		/*
		 * try{ InvAlertOps ivo = new InvAlertOps(); ivo.CheckInventoryStatus(tcon,
		 * Arrays.asList(rs.getInt(1)));
		 *
		 * ivo = null;
		 *
		 * }catch(Exception e){ logger.error(Exceptions.giveStackTrace(e)); }
		 */

	}
}
