package com.oreilly.common.interaction.text.validator;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.ValidationFailedError;


public class HashMapKeyValidator< T, U > extends Validator {
	
	protected HashMap< T, U > target = null;
	
	
	public HashMapKeyValidator( HashMap< T, U > target ) {
		this.target = target;
	}
	
	
	@Override
	protected Object validate( Object object, InteractionPage page ) throws ValidationFailedError {
		if ( target.keySet().contains( object ) )
			return object;
		// test with all lower case
		String test = object.toString().toLowerCase().trim();
		for ( T key : target.keySet() )
			if ( test.contentEquals( key.toString().toLowerCase().trim() ) )
				return object;
		// not found at all? Error.
		throw new ValidationFailedError( this, "Input must be one of: " + StringUtils.join( target.keySet(), ", " ) );
	}
}
