package com.luvbrite.repository;

import java.util.List;

import com.luvbrite.model.AuthoritiesDTO;
import com.luvbrite.model.PermissionDTO;
import com.luvbrite.model.UserDetails;

public interface IOperatorRepository {

	int saveOperator(UserDetails operator);

	List<UserDetails> getOperatorsDataByShopId(Integer shopId);

	int updateOperatorById(int id, UserDetails operator);

	int deleteOperatorById(Integer id);

	int updatePwdByOperatorId(Integer id, String password);

	List<String> getListOfAllPermissions();

	List<String> getListOfAllowedPermissionById(Integer id);

	String getUserTypeById(Integer id);

	int authoritiesGrantByUserId(AuthoritiesDTO authorities);

	int permissionGrantByUserId(PermissionDTO permission);

}
