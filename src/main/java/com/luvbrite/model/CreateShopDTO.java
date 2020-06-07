package com.luvbrite.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateShopDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4369680558933760222L;
	
	private Integer id;
	private String fullName;
	private String shopName;
	private String shopAdminId;
	private String shopOwnerName;
	private String userName;
	private String password;
	private String email;
	private String ownerId;
	private String domain;
	private String shopId;
	private String userTypeId;
	private Integer createdBy;
	private String createdByName;
	private Boolean isActive;
}
