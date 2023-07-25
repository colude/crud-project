package com.springboot.crud.plasse.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@AllArgsConstructor
@Jacksonized
public class ApiException {
	
	private final int code;
	private final HttpStatus httpStatus;
	private final Map<String, String> errors;
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss a")
	private final LocalDateTime timeStamp;
}
