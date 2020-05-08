package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.UserDetails;

public interface IOperatorRepository {

	int saveOperator(UserDetails operator);

	List<UserDetails> getOperatorsDataByShopId(Integer shopId);

}
