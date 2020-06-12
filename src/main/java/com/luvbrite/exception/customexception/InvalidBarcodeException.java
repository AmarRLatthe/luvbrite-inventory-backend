package com.luvbrite.exception.customexception;

public class InvalidBarcodeException extends Exception{

	private static final long serialVersionUID = 912309123l;

	public InvalidBarcodeException(String errorMessage) {
		super(errorMessage);
	}

}
