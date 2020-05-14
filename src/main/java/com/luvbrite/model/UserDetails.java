package com.luvbrite.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDetails {

	private Integer id;
	private String name;
	private String username;
	private String password;
	private String email;

	private String shopName;
	private String userType;
	private Integer userTypeId;
	private Integer ownerId;
	private Integer shopId;

	private Boolean isActive;

	private List<String> userRoles;
	
	private Integer createdBy;
	private String createdByName;

	private String createdAt;
}
