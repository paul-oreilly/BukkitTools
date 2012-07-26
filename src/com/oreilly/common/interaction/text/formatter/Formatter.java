package com.oreilly.common.interaction.text.formatter;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;


abstract public class Formatter {
	
	public Formatter nextInChain = null;
	
	
	// chained init functions...
	
	public Formatter chain( Formatter formatter ) {
		if ( nextInChain == null )
			this.nextInChain = formatter;
		else
			nextInChain.chain( formatter );
		return this;
	}
	
	
	public String startFormatting( String s, InteractionPage page, Interaction interaction ) {
		s = format( s, page, interaction );
		if ( nextInChain != null )
			s = nextInChain.startFormatting( s, page, interaction );
		return s;
	}
	
	
	abstract protected String format( String s, InteractionPage page, Interaction interaction );
	
}
