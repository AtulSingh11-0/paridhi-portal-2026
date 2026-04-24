package com.megatronix.paridhi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.USER_ALREADY_EXISTS;

@ResponseStatus ( HttpStatus.CONFLICT )
public class UserAlreadyExistsException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException () {
		super(USER_ALREADY_EXISTS);
	}

	public UserAlreadyExistsException ( String message ) {
		super(message);
	}
}
