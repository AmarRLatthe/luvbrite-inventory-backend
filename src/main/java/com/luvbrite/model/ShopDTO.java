package com.luvbrite.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShopDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 2261205648285873476L;

	private Integer shopId;
	private String shopName;
	private String shopOwnerName;
	private String createdBy;
	private String domain;
	private String email;
	private String shopOwnerUsername;
	
	
}
