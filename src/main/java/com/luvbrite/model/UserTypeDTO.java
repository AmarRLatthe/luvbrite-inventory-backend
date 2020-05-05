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
public class UserTypeDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4781759525103307653L;
	private Integer id;
	private String userType;

}
