package com.oreilly.common.interaction.text.error;

public class ContextDataRequired extends Exception {
	
	private static final long serialVersionUID = 3784449633184491581L;
	public String key = null;
	public String classType = null;
	
	
	@SuppressWarnings("rawtypes")
	public ContextDataRequired( String requiresKey, Class requiredType ) {
		this.key = requiresKey;
		this.classType = requiredType.toString();
		// DEBUG:
		System.out.println( "com.oreilly.common ERROR: Context data required, " + requiresKey + " as " +
				requiredType.toString() );
	}
	
}
