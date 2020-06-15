package com.luvbrite.repository;

import com.luvbrite.model.PaginatedReturns;

import java.util.List;

public interface IReturnsRepository {

	List<String> listReturnReasons(Integer shopId);

	PaginatedReturns listReturns(Integer currentPage,Integer shopId) throws Exception;

	public int deleteReturn(Integer  returnId, Integer shopId,Integer operatorId) throws  Exception;
}
