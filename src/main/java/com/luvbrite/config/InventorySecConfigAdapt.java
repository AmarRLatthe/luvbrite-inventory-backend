package com.luvbrite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

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
    	http.cors().disable();
    	http.csrf().disable();
//    	http.authorizeRequests().anyRequest().permitAll();
    	http
    	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    	.and()
    		.authorizeRequests().antMatchers("/api/user/signIn").permitAll()
    		.and()
    		.authorizeRequests()
    			.antMatchers("/api/shop/createShop").hasAnyRole("CHIEF","CREATE_SHOP")
    			.antMatchers("/api/driver/createDriver").hasAnyRole("CHIEF","CREATE_DRIVER","SHOP_CHIEF")
    			.antMatchers("/api/vendor/createVendor").hasAnyRole("CHIEF","CREATE_VENDOR","SHOP_CHIEF")
    			.antMatchers("/api/driver/getAllDriverByShop").hasAnyRole("CHIEF","SHOP_CHIEF","CHIEF_MANAGER","MANAGER")
    			.antMatchers("/api/vendor/getAllVendorByShop").hasAnyRole("CHIEF","SHOP_CHIEF","CHIEF_MANAGER","MANAGER")
    			.antMatchers("/api/operator/createOperator").hasAnyRole("CHIEF","SHOP_CHIEF","CREATE_OPERATOR")
    			.antMatchers("/api/shop/getAllShops").hasAnyRole("CHIEF")
    			.anyRequest().authenticated()
    		.and()
    		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
    		.and()
    		.apply(new JwtSecurityConfigurer(jwtTokenProvider));
    	
    	http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);

    }

    
    public BCryptPasswordEncoder encoder() {
    	return new BCryptPasswordEncoder();
    }
}
