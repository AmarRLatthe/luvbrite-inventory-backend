package com.luvbrite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.luvbrite.repository.IUserRepository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@NoArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private IUserRepository iUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		com.luvbrite.model.UserDetails userDetails = this.iUserRepository.findByUsername(username);
		log.info("userEntity is {}", userDetails);
		if (userDetails == null) {
			throw new UsernameNotFoundException("Username: " + username + " not found");
		}
		return new CustomUserDetails(userDetails);
	}
}