package com.example.movieticketsclientapp;

public class ClientChainCodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String errorMessage;
	
	public ClientChainCodeException(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
}
