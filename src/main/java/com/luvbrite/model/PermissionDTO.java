package com.luvbrite.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PermissionDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -7407596215350265281L;

	private Integer id;
	private String[] permissions;
	
}
