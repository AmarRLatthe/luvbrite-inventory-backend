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
public class AuthoritiesDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -4668961965896218582L;
	private Integer id;
	private String[] authorities;
 	
}
