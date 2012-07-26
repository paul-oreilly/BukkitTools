package com.oreilly.common.interaction.text.formatter;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionFactory;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.text.ColorTool;


public class Highlight extends Formatter {
	
	public static final String STYLE_KEY = "highlighter-styles";
	protected static final String HIGHLIGHT_TAG_OPENING = "<Highlight";
	protected static final String HIGHLIGHT_TAG_CLOSING = "</Highlight";
	
	
	// for client use, to add tags
	public static String startHighlight( String type ) {
		return HIGHLIGHT_TAG_OPENING + " " + type.toLowerCase().trim() + ">";
	}
	
	
	public static String endHighlight( String type ) {
		return HIGHLIGHT_TAG_CLOSING + " " + type.toLowerCase().trim() + ">";
	}
	
	
	public static String HighlightAs( String type, String content ) {
		return startHighlight( type ) + content + endHighlight( type );
	}
	
	
	// for adding style mappings to a interaction / factory
	
	public static void addHighlightStyle( InteractionFactory factory, String type, ChatColor... styles ) {
		addHighlightStyle( factory.style, type, styles );
	}
	
	
	public static void addHighlightStyle( Interaction interaction, String type, ChatColor... styles ) {
		addHighlightStyle( interaction.style, type, styles );
	}
	
	
	@SuppressWarnings("unchecked")
	public static void addHighlightStyle( HashMap< String, Object > styleMap, String type, ChatColor... styles ) {
		Object obj = styleMap.get( STYLE_KEY );
		HashMap< String, ArrayList< ChatColor >> styleMaster = null;
		if ( obj == null ) {
			styleMaster = new HashMap< String, ArrayList< ChatColor >>();
			styleMap.put( STYLE_KEY, styleMaster );
		} else {
			try {
				styleMaster = (HashMap< String, ArrayList< ChatColor >>)obj;
			} catch ( ClassCastException error ) {
				// TODO: Error message
				return;
			}
		}
		ArrayList< ChatColor > styleList = styleMaster.get( type.toLowerCase().trim() );
		if ( styleList == null ) {
			styleList = new ArrayList< ChatColor >();
			styleMaster.put( type.toLowerCase().trim(), styleList );
		} // TODO: info note if adding to existing style
		for ( ChatColor item : styles )
			styleList.add( item );
	}
	
	public ChatColor defaultHighlightColor = ChatColor.BLUE;
	
	
	// chained init methods
	
	public Highlight() {
		super();
	}
	
	
	public Highlight withDefaultHighlight( ChatColor color ) {
		defaultHighlightColor = color;
		return this;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected String format( String s, InteractionPage page, Interaction interaction ) {
		Object obj = page.style.get( STYLE_KEY );
		HashMap< String, ArrayList< ChatColor >> styleMaster = null;
		try {
			styleMaster = (HashMap< String, ArrayList< ChatColor >>)obj;
		} catch ( ClassCastException error ) {
			styleMaster = null;
		}
		// if we have styles, loop over them and convert highlighter tags to style tags
		if ( styleMaster != null )
			for ( String key : styleMaster.keySet() ) {
				ArrayList< ChatColor > styles = styleMaster.get( key );
				String openingStyle = "";
				String closingStyle = "";
				for ( ChatColor color : styles ) {
					openingStyle += ColorTool.begin( color );
					closingStyle += ColorTool.end();
				}
				s = s.replace( startHighlight( key ), openingStyle );
				s = s.replace( endHighlight( key ), closingStyle );
			}
		// for any remaining highlighter tags, convert to the default style
		String result = "";
		int lastIndex = 0;
		int index = s.indexOf( HIGHLIGHT_TAG_OPENING );
		while ( index != -1 ) {
			// copy the last fragment
			result += s.substring( lastIndex, index );
			// update the lastIndex to the end of the tag
			lastIndex = s.indexOf( ">", index + HIGHLIGHT_TAG_OPENING.length() + 1 );
			// and index to the next tag
			index = s.indexOf( HIGHLIGHT_TAG_OPENING, lastIndex );
			// add the default style
			result += ColorTool.begin( defaultHighlightColor );
		}
		// copy the last fragment
		result += s.substring( lastIndex );
		// pull result back into the buffer
		s = result;
		// and do a similar loop to close off remaining highlighter tags
		lastIndex = 0;
		index = s.indexOf( HIGHLIGHT_TAG_CLOSING );
		while ( index != -1 ) {
			// copy the last fragment
			result += s.substring( lastIndex, index );
			// update the indexes
			lastIndex = s.indexOf( ">", index + HIGHLIGHT_TAG_CLOSING.length() + 1 );
			index = s.indexOf( HIGHLIGHT_TAG_CLOSING, lastIndex );
			// add the default style
			result += ColorTool.end();
		}
		// copy the last fragment
		result += s.substring( lastIndex );
		return result;
	}
}
