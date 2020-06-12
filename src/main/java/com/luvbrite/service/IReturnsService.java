package com.luvbrite.service;

public interface IReturnsService {

	public int addReturn(String packetCode, String reason, Integer shopId, Integer operatorId) throws Exception;
}
