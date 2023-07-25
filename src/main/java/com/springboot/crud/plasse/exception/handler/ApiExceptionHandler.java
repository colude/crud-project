package com.springboot.crud.plasse.exception.handler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.springboot.crud.plasse.exception.ApiException;
import com.springboot.crud.plasse.exception.UserNotFoundException;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
public class ApiExceptionHandler {
	
	private static final int CODE_HTTP_404 = 404;
	
	@ExceptionHandler(value = {UserNotFoundException.class})
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {	
		Map<String, String> errors = Collections.singletonMap( "message" , e.getMessage());
		
		ApiException apiException = new ApiException(CODE_HTTP_404, HttpStatus.NOT_FOUND, errors, LocalDateTime.now());
		return new ResponseEntity<>(apiException, NOT_FOUND);
	}
}