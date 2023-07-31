package com.springboot.crud.plasse.exception.handler;

import com.springboot.crud.plasse.exception.ApiException;
import com.springboot.crud.plasse.exception.ApiRequestException;
import com.springboot.crud.plasse.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
public class ApiExceptionHandler {
	
	private static final int CODE_HTTP_400 = 400;
	private static final int CODE_HTTP_404 = 404;
	private static final int CODE_HTTP_405 = 405;

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<Object> handleNoHandlerFound(NoHandlerFoundException e, WebRequest request) {
		return handleAnyException(CODE_HTTP_404, NOT_FOUND, e);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleError405(HttpServletRequest request, Exception e) {
		return handleAnyException(CODE_HTTP_405, METHOD_NOT_ALLOWED, e);
	}

	@ExceptionHandler(value = {UserNotFoundException.class})
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
		return handleAnyException(CODE_HTTP_404, NOT_FOUND, e);
	}
	
	@ExceptionHandler(value = {ApiRequestException.class})
	public ResponseEntity<Object> handleApiRequestException(ApiRequestException e, WebRequest request) {
		return handleAnyException(CODE_HTTP_400, BAD_REQUEST, e);
	}

	public ResponseEntity<Object> handleAnyException(int code, HttpStatus httpStatus, Exception e) {
		Map<String, String> errors = Collections.singletonMap( "message" , e.getMessage());
		ApiException apiException = new ApiException(code, httpStatus, errors, LocalDateTime.now());
		return new ResponseEntity<>(apiException, httpStatus);
	}
}