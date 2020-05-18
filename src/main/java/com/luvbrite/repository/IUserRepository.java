package com.luvbrite.repository;

import com.luvbrite.model.UserDetails;

public interface IUserRepository {

	UserDetails findByUsername(String username);

	UserDetails findByEmail(String email);

	int countUserByUserName(String userName);

	int countUserByEmail(String email);

	int countUserByUserNameNId(String shopOwnerUsername, Integer id);

	int countUserByEmailNId(Integer id, String email);

}
