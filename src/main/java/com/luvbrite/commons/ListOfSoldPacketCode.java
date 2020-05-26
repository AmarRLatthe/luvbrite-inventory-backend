package com.luvbrite.commons;

import java.util.List;
import java.util.stream.Collectors;

import com.luvbrite.model.DeliveredPacketDTO;
import com.luvbrite.model.SoldPacketsDTO;



public class ListOfSoldPacketCode {
	public static List retrievePacketIds(List<SoldPacketsDTO> list) {
		List<Integer> packetCodeList = list.stream().map(SoldPacketsDTO::getId).collect(Collectors.toList());
		return packetCodeList;
	}

	public static List retrieveProductNames(List<DeliveredPacketDTO> list) {
		List<String> productNameList = list.stream().map(DeliveredPacketDTO::getProductName).collect(Collectors.toList());
		return productNameList;
	}

	public static List retrieveQuantityForEachProduct(List<DeliveredPacketDTO> list) {
		List<Integer> productQuantityList = list.stream().map(DeliveredPacketDTO::getItems).collect(Collectors.toList());
		return productQuantityList;
	}

	public static String getCommaSeparatedValues(List<String> list, boolean isProductName) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {

			if (isProductName) {
				String productName = list.get(i);
				sb.append(productName);
			} else {
				String packetCode = "" + list.get(i) + "";
				sb.append(packetCode);
			}
			if (i < (list.size() - 1)) {
				sb.append(" , \n");
			}
		}
		return sb.toString();

	}


	public static String getItemNumberCommaSeparated(List<Integer> list) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {

			String itemNumber = "" + list.get(i) + "";
			sb.append(itemNumber);
			if (i < (list.size() - 1)) {
				sb.append(" , \n");
			}
		}
		return sb.toString();

	}

	public static String getCommaSeparatedPacketIds(List<Integer> list) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {

			String packetIds = "" +list.get(i) + "";
			sb.append(packetIds);
			if (i < (list.size() - 1)) {
				sb.append(" , \n");
			}
		}
		return sb.toString();

	}
}
