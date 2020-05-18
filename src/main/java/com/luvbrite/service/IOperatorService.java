package com.luvbrite.service;

import java.util.List;
import java.util.Map;

import com.luvbrite.model.UserDetails;

public interface IOperatorService {

	int saveOperator(UserDetails operator);

	List<UserDetails> getOperatorsDataByShopId(Integer shopId);

	Map<String, Object> validateOperator(UserDetails operator);

	int updateOperatorById(int id, UserDetails operator);

	int deleteOperatorById(Integer id);

	int updatePwdByOperatorId(Integer id, String password);

}
