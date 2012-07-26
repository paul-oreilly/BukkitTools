package com.oreilly.common.interaction.text;

abstract public class TitledInteractionPage extends InteractionPage {
	
	public final static String TITLE_OVERRIDE = "titledInteractionPage_TitleOverride";
	
	protected String defaultTitle = "Title";
	
	
	public String getTitle( Interaction interaction ) {
		Object overrideObj = interaction.context.get( TITLE_OVERRIDE );
		if ( overrideObj != null )
			return overrideObj.toString();
		else
			return defaultTitle;
	}
	
}
