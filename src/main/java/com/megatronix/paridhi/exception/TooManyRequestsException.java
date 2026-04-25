package com.megatronix.paridhi.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@ResponseStatus(code = TOO_MANY_REQUESTS, reason = "Too many requests. Please try again later.")
public class TooManyRequestsException extends RuntimeException {
	public TooManyRequestsException() {
		super("Too many requests. Please try again later.");
	}

	public TooManyRequestsException(String message) {
		super(message);
	}
}
