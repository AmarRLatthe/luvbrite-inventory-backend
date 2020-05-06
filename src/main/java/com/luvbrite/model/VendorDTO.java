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
public class VendorDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 7485227642966575021L;
	private Integer vendorId;
	private String vendorName;
	private String company;
	private String address;
	private String city;
	private String state;
	private String zipcode;
	private String phone;
	private String email;
	private String website;
	private String dateAdded;
	private Integer shopId;
	private Integer createdBy;
	private String createdByName;
	
	
}
