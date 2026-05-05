package com.megatronix.paridhi.exception;

import com.megatronix.paridhi.constant.MessageConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.TOO_MANY_REQUESTS;

@ResponseStatus ( code = HttpStatus.TOO_MANY_REQUESTS, reason = MessageConstant.UserMessage.TOO_MANY_REQUESTS )
public class TooManyRequestsException extends RuntimeException {

	public TooManyRequestsException () {
		super(TOO_MANY_REQUESTS);
	}

	public TooManyRequestsException ( String message ) {
		super(message);
	}
}
