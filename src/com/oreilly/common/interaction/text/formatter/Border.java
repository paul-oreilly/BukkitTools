package com.oreilly.common.interaction.text.formatter;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.TitledInteractionPage;


// TODO: Interface with style's based on context data

public class Border extends Formatter {
	
	static public final String CHARACTER_LINES = "border_line_character";
	static public final String CHARACTER_TOP_RIGHT = "border_top_right_character";
	static public final String CHARACTER_TOP_LEFT = "border_top_left_character";
	static public final String CHARACTER_BOTTOM_RIGHT = "border_bottom_right_character";
	static public final String CHARACTER_BOTTOM_LEFT = "border_bottom_left_charactr";
	
	static public final String COLOR_TEXT_BORDER = "border_color_text_outline";
	static public final String COLOR_TITLE_BORDER = "border_color_title_outline";
	static public final String COLOR_TITLE_TEXT = "border_color_title_text";
	
	enum LineType {
		TITLE_TOP, TITLE_BOTTOM, TEXT_TOP, TEXT_BOTTOM
	}
	
	
	public Border() {
	}
	
	
	// main function
	
	protected HashMap< String, String > getCharacterStyles( InteractionPage page ) {
		// when styles are not provided, use the defaults
		HashMap< String, String > result = new HashMap< String, String >();
		// for each item, check if a style is defined in page.style.. if not, use a default.
		result.put( CHARACTER_LINES, getOrDefaultCharacter( CHARACTER_LINES, page, '-' ) );
		result.put( CHARACTER_TOP_RIGHT, getOrDefaultCharacter( CHARACTER_TOP_RIGHT, page, '\\' ) );
		result.put( CHARACTER_TOP_LEFT, getOrDefaultCharacter( CHARACTER_TOP_LEFT, page, '/' ) );
		result.put( CHARACTER_BOTTOM_RIGHT, getOrDefaultCharacter( CHARACTER_BOTTOM_RIGHT, page, '/' ) );
		result.put( CHARACTER_BOTTOM_LEFT, getOrDefaultCharacter( CHARACTER_BOTTOM_LEFT, page, '\\' ) );
		return result;
	}
	
	
	protected HashMap< String, String > getChatColorStyles( InteractionPage page ) {
		HashMap< String, String > result = new HashMap< String, String >();
		result.put( COLOR_TEXT_BORDER, getOrDefaultChatColor( COLOR_TEXT_BORDER, page, ChatColor.DARK_GREEN ) );
		result.put( COLOR_TITLE_BORDER, getOrDefaultChatColor( COLOR_TITLE_BORDER, page, ChatColor.DARK_BLUE ) );
		result.put( COLOR_TITLE_TEXT, getOrDefaultChatColor( COLOR_TITLE_TEXT, page, ChatColor.AQUA ) );
		return result;
	}
	
	
	private String getOrDefaultCharacter( String key, InteractionPage page, Character defaultValue ) {
		Object object = page.style.get( key );
		if ( object != null )
			if ( object instanceof Character )
				return ( (Character)object ).toString();
		return defaultValue.toString();
	}
	
	
	private String getOrDefaultChatColor( String key, InteractionPage page, ChatColor defaultValue ) {
		Object object = page.style.get( key );
		if ( object != null )
			if ( object instanceof ChatColor )
				return ( (ChatColor)object ).toString();
		return defaultValue.toString();
	}
	
	
	@Override
	protected String format( String s, InteractionPage page, Interaction interaction ) {
		String result = "";
		HashMap< String, String > characterStyles = getCharacterStyles( page );
		HashMap< String, String > colorStyles = getChatColorStyles( page );
		// title, if one exists
		if ( page instanceof TitledInteractionPage ) {
			result += makeLine( LineType.TITLE_TOP, characterStyles, colorStyles );
			TitledInteractionPage withTitle = (TitledInteractionPage)page;
			result += colorStyles.get( COLOR_TITLE_TEXT ) + "  " + withTitle.getTitle( interaction ) + "\n";
			result += makeLine( LineType.TITLE_BOTTOM, characterStyles, colorStyles );
		}
		// page body
		result += makeLine( LineType.TEXT_TOP, characterStyles, colorStyles );
		for ( String line : s.split( "\n" ) )
			result += "  " + line + "\n";
		// lower line
		result += makeLine( LineType.TEXT_BOTTOM, characterStyles, colorStyles );
		return result;
	}
	
	
	protected String makeLine( LineType lineType, HashMap< String, String > characterStyles,
			HashMap< String, String > colorStyles ) {
		String leftCorner = "";
		String rightCorner = "";
		String color = "";
		switch ( lineType ) {
			case TEXT_TOP:
				leftCorner = characterStyles.get( CHARACTER_TOP_LEFT );
				rightCorner = characterStyles.get( CHARACTER_TOP_RIGHT );
				color = colorStyles.get( COLOR_TEXT_BORDER );
				break;
			case TITLE_TOP:
				leftCorner = characterStyles.get( CHARACTER_TOP_LEFT );
				rightCorner = characterStyles.get( CHARACTER_TOP_RIGHT );
				color = colorStyles.get( COLOR_TITLE_BORDER );
				break;
			case TEXT_BOTTOM:
				leftCorner = characterStyles.get( CHARACTER_BOTTOM_LEFT );
				rightCorner = characterStyles.get( CHARACTER_BOTTOM_RIGHT );
				color = colorStyles.get( COLOR_TEXT_BORDER );
				break;
			case TITLE_BOTTOM:
				leftCorner = characterStyles.get( CHARACTER_BOTTOM_LEFT );
				rightCorner = characterStyles.get( CHARACTER_BOTTOM_RIGHT );
				color = colorStyles.get( COLOR_TITLE_BORDER );
				break;
		}
		return color + leftCorner +
				StringUtils.repeat( characterStyles.get( CHARACTER_LINES ), 51 ) +
				rightCorner + "\n";
	}
	
}