package com.luvbrite.repository;

import com.luvbrite.model.UserDetails;

public interface IUserRepository {

	UserDetails findByUsername(String username);

}
