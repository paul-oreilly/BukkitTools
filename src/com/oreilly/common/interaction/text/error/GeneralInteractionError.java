package com.oreilly.common.interaction.text.error;

public class GeneralInteractionError extends Exception {
	
	private static final long serialVersionUID = 2402614125393780973L;
	public String reason = null;
	
	
	public GeneralInteractionError( String reason ) {
		this.reason = reason;
		// DEBUG:
		System.out.println( "com.oreilly.common ERROR: General display error: " + reason );
	}
}
