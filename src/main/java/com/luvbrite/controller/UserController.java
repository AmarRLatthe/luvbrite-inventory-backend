package com.luvbrite.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.config.jwt.JwtTokenProvider;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user/")
@Slf4j
//@CrossOrigin(allowedHeaders = "*",origins = "*")
public class UserController {

	@Autowired
	private IUserService iUserService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private AuthenticationManager authenticationManager;



	@PostMapping("/signIn")
	public ResponseEntity<Object> signIn(@RequestBody Map<String, String> map) {
		log.info("username is {} and password is {}", map.get("username"), map.get("password"));
		log.info(" pass {}", new BCryptPasswordEncoder().encode("12345"));
		if (!map.get("password").isEmpty() || !map.get("username").isEmpty()) {
			try {
				String username = map.get("username");
				log.info("authentication manager {}", authenticationManager);
				authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(username, map.get("password")));
				
				log.info("{} and {}", username, map.get("password"));
				
				
				UserDetails userDetails = iUserService.getByUsername(username);
				if (userDetails!=null) {
					
					String token = jwtTokenProvider.createToken(username, userDetails.getUserRoles());

					Map<Object, Object> model = new HashMap<>();
					model.put("isLoggedIn",true);
					model.put("username", userDetails.getUsername());
					model.put("token", token);
					model.put("userType",userDetails.getUserType());
					return ok(model);
				}

			} catch (AuthenticationException e) {
				log.info("message is {} and exception is {}", e.getMessage(), e);
				throw new BadCredentialsException("Invalid username/password supplied");
			}
		}
		return null;
	}

	
}
