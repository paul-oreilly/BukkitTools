package com.oreilly.common.interaction.text.error;

import com.oreilly.common.interaction.text.validator.Validator;


public class ValidationFailedError extends Exception {
	
	private static final long serialVersionUID = 3800812920634908917L;
	public String message = null;
	public Validator source = null;
	
	
	public ValidationFailedError( Validator source, String message ) {
		this.source = source;
		this.message = message;
	}
}
