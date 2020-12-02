package com.example.movieticketsclientapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ClientChainCodeExceptionController {

	@ExceptionHandler(value = ClientChainCodeException.class)
	public ResponseEntity<Object> exception(ClientChainCodeException exception) {
		return new ResponseEntity<>(exception.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
