package com.luvbrite.commonresponse;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommonResponse implements Serializable{
	private static final long serialVersionUID = 3209314339580785570L;

	private Integer code;
	private String status;
	private String message;
	private Object data;
}
