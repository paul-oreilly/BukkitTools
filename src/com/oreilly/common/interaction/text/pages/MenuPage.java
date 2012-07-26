package com.oreilly.common.interaction.text.pages;

import java.util.HashMap;
import java.util.HashSet;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.common.interaction.text.interfaces.Choices;
import com.oreilly.common.interaction.text.validator.ChoicesValidator;


abstract public class MenuPage extends TitledInteractionPage implements Choices {
	
	public HashMap< String, InteractionPage[] > choices = new HashMap< String, InteractionPage[] >();
	public boolean loopbackOnCompletion = false;
	
	
	public MenuPage() {
		super();
		withValidator( new ChoicesValidator() );
	}
	
	
	@Override
	public HashSet< String > getChoices() {
		HashSet< String > result = new HashSet< String >();
		result.addAll( choices.keySet() );
		return result;
	}
	
	
	// chained init methods
	
	public MenuPage withChoice( String choice, InteractionPage... result ) {
		choices.put( choice, result );
		return this;
	}
	
	
	public MenuPage withAlias( String choice, String alias ) {
		choices.put( alias, choices.get( choice ) );
		return this;
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) {
		InteractionPage[] interactionLinks = choices.get( data.toString() );
		if ( interactionLinks != null )
			interaction.addPages( interactionLinks );
		if ( loopbackOnCompletion )
			interaction.pages.add( this );
		return null;
	}
}
