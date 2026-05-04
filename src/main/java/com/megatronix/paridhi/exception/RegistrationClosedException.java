package com.megatronix.paridhi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus ( HttpStatus.FORBIDDEN )
public class RegistrationClosedException extends RuntimeException {
	public RegistrationClosedException ( String message ) {
		super(message);
	}
}
