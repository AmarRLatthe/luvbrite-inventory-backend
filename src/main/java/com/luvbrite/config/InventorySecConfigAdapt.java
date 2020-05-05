package com.luvbrite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.luvbrite.config.jwt.JwtAuthenticationEntryPoint;
import com.luvbrite.config.jwt.JwtSecurityConfigurer;
import com.luvbrite.config.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
//@EnableJpaRepositories(basePackageClasses = IUserRepository.class)
public class InventorySecConfigAdapt extends WebSecurityConfigurerAdapter {

	@Autowired
    private JwtTokenProvider jwtTokenProvider;

	@Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
	
 
	
	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
////        http.csrf().disable();
//        
////        http.authorizeRequests().anyRequest().permitAll();
//        http.authorizeRequests().antMatchers("/api/user/signIn").permitAll();
////        http.authorizeRequests().antMatchers("api/shop/createShop").hasRole("CHIEF");
//        http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
//        http.apply(new JwtSecurityConfigurer(jwtTokenProvider));
//        
    	
    	http.httpBasic().disable()
    	.cors().and()
    	.csrf().disable()
    	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    	.and()
    		.authorizeRequests().antMatchers("/api/user/signIn").permitAll()
    		.and()
    		.authorizeRequests()
    			.antMatchers("/api/shop/createShop").hasAnyRole("CHIEF","ROLE_CREATE_SHOP").anyRequest().authenticated()
    		.and()
    		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
    		.and()
    		.apply(new JwtSecurityConfigurer(jwtTokenProvider));

    }

    public BCryptPasswordEncoder encoder() {
    	return new BCryptPasswordEncoder();
    }
}
