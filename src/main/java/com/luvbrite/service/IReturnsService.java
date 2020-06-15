package com.luvbrite.service;

import com.luvbrite.model.PaginatedReturns;

import java.util.List;

public interface IReturnsService {

    int addReturn(String packetCode, String reason, Integer shopId, Integer operatorId) throws Exception;

    List<String> listReasons(Integer shopId);

    PaginatedReturns listReturns(Integer currentPage, Integer shopId) throws Exception;

    int  deleteReturn(Integer returnId,Integer shopId,Integer operatorId) throws Exception;

}
