package com.luvbrite.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PasswordDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 911459162706042031L;
	
	private String password;
	private String confirmPassword;
}
