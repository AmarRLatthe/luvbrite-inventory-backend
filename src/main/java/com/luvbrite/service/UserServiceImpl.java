package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.UserDetails;
import com.luvbrite.repository.IUserRepository;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepository iUserRepository;
	 
	
	@Override
	public UserDetails getByUsername(String username) {
		return iUserRepository.findByUsername(username);
	}

}
