package com.megatronix.paridhi.exception;

public class ProfileNotYetCreatedException extends RuntimeException {
	public ProfileNotYetCreatedException() {
		super("You needs to create a profile first.");
	}
	
	public ProfileNotYetCreatedException(String message, Throwable cause) {
		super(message, cause);
	}
}
