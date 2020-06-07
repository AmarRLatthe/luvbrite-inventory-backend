package com.luvbrite.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenAuthenticationFilter extends GenericFilterBean {

	private JwtTokenProvider jwtTokenProvider;

	public JwtTokenAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
			log.info("token is {}",token);
			if (token != null) {
				boolean isValidToken = jwtTokenProvider.validateToken(token);
				log.info("is valide Token {} ",isValidToken);
				if (token != null && isValidToken) {
				
					Authentication auth = jwtTokenProvider.getAuthentication(token);
					log.info("Get Authentication {} ",auth);
					if (auth != null) {
						SecurityContextHolder.getContext().setAuthentication(auth);
					}
				}
			}
			log.info("going to do filter..");
			
			filterChain.doFilter(req, res);
		} catch (ExpiredJwtException ex) {
			log.info("message is {} and exception is {}",ex.getMessage(), ex);
			((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
		} catch (Exception e) {
			log.info("message is {} and exception is {}", e.getMessage(), e);
			filterChain.doFilter(req, res);
		}
	}
}
