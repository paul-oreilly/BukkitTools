package com.oreilly.common.interaction.text.error;

public class AbortInteraction extends Exception {
	
	private static final long serialVersionUID = 4928228335111054800L;
	public String message = null;
	
	
	public AbortInteraction( String message ) {
		this.message = message;
	}
	
	
	public AbortInteraction() {
	}
	
}
