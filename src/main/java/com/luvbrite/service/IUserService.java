package com.luvbrite.service;

import com.luvbrite.model.UserDetails;

public interface IUserService {

	UserDetails getByUsername(String username);

}
