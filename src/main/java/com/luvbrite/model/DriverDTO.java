package com.luvbrite.model;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DriverDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 5842553509834786178L;
	
	private Integer id;
	private String  driverName;
	private String 	userName;
	private String dateAdded;
	private String phoneNumber;
	private Integer status;
	private Integer shopId;
	private Integer createdBy;
	private String createdByName;

	
}
