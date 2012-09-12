package com.oreilly.common.text;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;


public class VariablePrefixer {
	
	public String prefix = null;
	
	
	public VariablePrefixer( String prefix ) {
		this.prefix = prefix + ".";
	}
	
	
	public VariablePrefixer( VariablePrefixer parent, String prefix ) {
		this.prefix = parent.prefix + prefix + ".";
	}
	
	
	public VariablePrefixer( InteractionPage page, Interaction interaction ) {
		prefix = page.getTranslationKey( interaction ) + ".";
	}
	
	
	public String define( String name ) {
		return VariableTool.variable( prefix + name );
	}
}
