package com.oreilly.common.interaction.text.helpers;

import java.util.ArrayList;

import com.oreilly.common.interaction.text.InteractionPage;


public class Choice {
	
	Choices parent = null;
	String text = null;
	
	
	public Choice( Choices parent, String text ) {
		this.parent = parent;
		if ( text.endsWith( "\n" ) )
			this.text = text;
		else
			this.text = text + "\n";
	}
	
	public Choice withAlias( String... aliasList ) {
		parent.withAlias(this, aliasList);
		return this;
	}
}


class ChoiceInternal extends Choice {
	
	String returnValue = null;
	
	
	public ChoiceInternal( Choices parent, String text, String returnValue ) {
		super( parent, text );
		this.returnValue = returnValue;
	}
}


class ChoicePage extends Choice {
	
	public ArrayList< InteractionPage > pages = new ArrayList< InteractionPage >();
	
	
	public ChoicePage( Choices parent, String text, InteractionPage... pages ) {
		super( parent, text );
		for ( InteractionPage page : pages )
			this.pages.add( page );
	}
}


class ChoiceAbort extends Choice {
	
	public ChoiceAbort( Choices parent, String text ) {
		super( parent, text );
	}
}