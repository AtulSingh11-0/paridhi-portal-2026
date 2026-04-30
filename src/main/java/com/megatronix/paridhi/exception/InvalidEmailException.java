package com.megatronix.paridhi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.INVALID_EMAIL;

@ResponseStatus ( value = HttpStatus.BAD_REQUEST, reason = "Invalid email address provided" )
public class InvalidEmailException extends RuntimeException {
	public InvalidEmailException () {
		super(INVALID_EMAIL);
	}
}
