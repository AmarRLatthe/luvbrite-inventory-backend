package com.luvbrite.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CorsFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	        response.setHeader("Access-Control-Max-Age", "3600");
	        response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
	        response.addHeader("Access-Control-Expose-Headers", "xsrf-token");
//	        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//			
//			if (csrf != null) {
//				Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
//				String token = csrf.getToken();
//				if (cookie==null || token!=null && !token.equals(cookie.getValue())) {
//					cookie = new Cookie("XSRF-TOKEN", token);
//					cookie.setPath("/");
//					response.addCookie(cookie);
//				}
//			}
	        if ("OPTIONS".equals(request.getMethod())) {
	        	log.info("method type is {}",request.getMethod());
	        	log.info("requested URL is {}",request.getRequestURI());
	            response.setStatus(HttpServletResponse.SC_OK);
	            
	        } else { 
	            filterChain.doFilter(request, response);
	        }
		
	}

}
