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
public class BarcodeSequenceDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8894914342620679817L;
	private Integer id;
	private String averyTemplate;
	private String objType;
	private String prefix;
	private Long nextVal;
}
