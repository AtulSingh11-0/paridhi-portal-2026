package com.megatronix.paridhi.exception;

public class InvalidOtpException extends RuntimeException {

	public InvalidOtpException () {
		super("Invalid request details.");
	}

	public InvalidOtpException ( String message ) {
		super(message);
	}
}
