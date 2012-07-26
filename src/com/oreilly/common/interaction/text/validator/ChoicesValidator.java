package com.oreilly.common.interaction.text.validator;

import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.InterfaceDependencyError;
import com.oreilly.common.interaction.text.error.ValidationFailedError;
import com.oreilly.common.interaction.text.interfaces.Choices;


public class ChoicesValidator extends Validator {
	
	@Override
	protected Object validate( Object object, InteractionPage page ) throws ValidationFailedError,
			InterfaceDependencyError {
		if ( page instanceof Choices ) {
			HashSet< String > choiceList = ( (Choices)page ).getChoices();
			if ( choiceList.contains( object ) )
				return object;
			throw new ValidationFailedError( this, "Input must be one of: " + StringUtils.join( choiceList, ", " ) );
		}
		else
			throw new InterfaceDependencyError( Choices.class.toString() );
	}
	
}
