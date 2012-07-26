package com.oreilly.common.text;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/*
 * A class to bring html like <tags> into strings, for nestled color replacement.
 * eg: "<color YELLOW>Hello <color RED>user</color>!" should end with the "!" colored yellow.
 */

public class Style {
	
	enum TagType {
		OPENING, CLOSING, NEWLINE;
		
		@Override
		public String toString() {
			switch ( this ) {
				case OPENING:
					return "opening";
				case CLOSING:
					return "closing";
				case NEWLINE:
					return "newline";
			}
			return "error";
		}
	}
	
	static class Tag {
		
		String text = null;
		TagType type = null;
		
		
		public Tag( String text, TagType type ) {
			this.text = text;
			this.type = type;
		}
	}
	
	static class TagIndex implements Comparable< TagIndex > {
		
		Tag tag = null;
		int index = 0;
		
		
		public TagIndex( Tag tag, int index ) {
			this.tag = tag;
			this.index = index;
		}
		
		
		@Override
		public int compareTo( TagIndex arg0 ) {
			return index - arg0.index;
		}
	}
	
	private static List< Tag > match = new LinkedList< Tag >();
	private static HashMap< String, String > colorTranslations = new HashMap< String, String >();
	
	static {
		match.add( new Tag( "<color", TagType.OPENING ) );
		match.add( new Tag( "<colour", TagType.OPENING ) );
		match.add( new Tag( "</color", TagType.CLOSING ) );
		match.add( new Tag( "</colour", TagType.CLOSING ) );
		match.add( new Tag( "\n", TagType.NEWLINE ) );
		
		for ( ChatColor color : ChatColor.values() ) {
			colorTranslations.put( color.toString(), color.toString() );
			colorTranslations.put( color.name().toLowerCase(), color.toString() );
			colorTranslations.put( String.valueOf( color.getChar() ), color.toString() );
			colorTranslations.put( String.valueOf( color.ordinal() ), color.toString() );
		}
	}
	
	
	public static String apply( String rawText ) {
		return apply( rawText, true );
	}
	
	
	public static String strip( String rawText ) {
		return apply( rawText, false );
	}
	
	
	public static void sendToPlayer( Player player, String rawText ) {
		String styled = apply( rawText, true );
		for ( String line : styled.split( "\n" ) )
			player.sendMessage( line );
	}
	
	
	protected static String apply( String rawText, boolean useStyles ) {
		LinkedList< String > styleStack = new LinkedList< String >();
		// the default fallback color is white.. 
		styleStack.add( ChatColor.WHITE.toString() );
		String result = styleStack.getLast();
		// scan over the text, and add / pop the stack as we encounter tags
		// make a list of all the tags
		LinkedList< TagIndex > matches = new LinkedList< TagIndex >();
		for ( Tag tag : match ) {
			int index = rawText.indexOf( tag.text );
			while ( index != -1 ) {
				matches.add( new TagIndex( tag, index ) );
				index = rawText.indexOf( tag.text, index + 1 );
			}
		}
		System.out.println( "com.oreilly.common.text.Style DEBUG: " + matches.size() + " style items" );
		// sort them
		Collections.sort( matches );
		// pass over the string, and
		//  - replace opening tags with their color (and add color to the stack)
		//  - replace closing tags, by removing the top color from the stack, and using the remaining color
		//  - add the current color after any newline
		int lastIndex = 0;
		for ( TagIndex delta : matches ) {
			switch ( delta.tag.type ) {
				case OPENING: {
					// initial math
					int colorTagStart = delta.index + delta.tag.text.length();
					int colorTagFinish = rawText.indexOf( ">", delta.index );
					// add the previous text, and update lastIndex
					result += rawText.substring( lastIndex, delta.index );
					lastIndex = colorTagFinish + 1;
					// get the color value from the tag
					if ( colorTagFinish == -1 )
						break;
					String colorTag = rawText.substring( colorTagStart, colorTagFinish ).toLowerCase().trim();
					String colorValue = colorTranslations.get( colorTag );
					if ( colorValue == null )
						break;
					styleStack.add( colorValue );
					if ( useStyles )
						result += colorValue;
					continue;
				}
				case CLOSING: {
					// add the previous text, and update lastIndex
					result += rawText.substring( lastIndex, delta.index );
					lastIndex = delta.index + delta.tag.text.length() + 1;
					// remove the current color, and add the previous one
					styleStack.removeLast();
					if ( useStyles )
						result += styleStack.get( styleStack.size() - 1 );
					continue;
				}
				case NEWLINE: {
					lastIndex = delta.index + 1;
					if ( useStyles )
						result += "\n" + styleStack.get( styleStack.size() - 1 );
					else
						result += "\n";
					continue;
				}
			}
		}
		// add the last part of the text
		result += rawText.substring( lastIndex );
		return result;
	}
	
	
	// public utility functions
	
	public String begins( ChatColor color ) {
		return "<color " + color.name() + ">";
	}
	
	
	public String ends() {
		return "</color>";
	}
	
}
