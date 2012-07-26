package com.oreilly.common.interaction.text;

import java.util.HashMap;

import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.formatter.Formatter;
import com.oreilly.common.interaction.text.validator.Validator;


abstract public class InteractionPage {
	
	static public final int MAX_LINES = 40;
	
	public Formatter formatter = null;
	public Validator validator = null;
	public String validationFailedMessage = null;
	public HashMap< String, Object > style = new HashMap< String, Object >();
	
	
	// used when more input is desired
	protected void waitForInput( Interaction interaction ) {
		interaction.pageWaitingForInput = true;
	}
	
	
	abstract public String getDisplayText( Interaction interaction ) throws ContextDataRequired, GeneralDisplayError;
	
	
	// if a string is returned, it is displayed to the player.
	abstract public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralDisplayError;
	
	
	// chained init methods
	
	public InteractionPage withFormatter( Formatter formatter ) {
		if ( this.formatter == null )
			this.formatter = formatter;
		else
			this.formatter.chain( formatter );
		return this;
	}
	
	
	public InteractionPage withValidator( Validator validator ) {
		if ( this.validator == null )
			this.validator = validator;
		else
			this.validator.chain( validator );
		return this;
	}
	
	
	public InteractionPage withReplacementFormatter( Formatter formatter ) {
		this.formatter = formatter;
		return this;
	}
	
	
	public InteractionPage withReplacementValidator( Validator validator ) {
		this.validator = validator;
		return this;
	}
	
	
	public InteractionPage withValidationFailedMessage( String message ) {
		this.validationFailedMessage = message;
		return this;
	}
	
	
	public InteractionPage withStyle( String key, Object style ) {
		this.style.put( key, style );
		return this;
	}
	
	
	public InteractionPage withStyles( HashMap< String, Object > source ) {
		style.putAll( source );
		return this;
	}
	
}
