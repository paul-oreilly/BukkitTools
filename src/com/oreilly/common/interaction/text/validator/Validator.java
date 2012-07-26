package com.oreilly.common.interaction.text.validator;

import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.InterfaceDependencyError;
import com.oreilly.common.interaction.text.error.ValidationFailedError;


abstract public class Validator {
	
	public Validator nextInChain = null;
	
	
	// chained init functions...
	
	public Validator chain( Validator validator ) {
		if ( nextInChain == null )
			this.nextInChain = validator;
		else
			nextInChain.chain( validator );
		return this;
	}
	
	
	public Object startValidation( Object object, InteractionPage page ) throws ValidationFailedError,
			InterfaceDependencyError {
		object = validate( object, page );
		if ( nextInChain != null )
			object = nextInChain.startValidation( object, page );
		return object;
	}
	
	
	abstract protected Object validate( Object object, InteractionPage page ) throws ValidationFailedError,
			InterfaceDependencyError;
	
}
