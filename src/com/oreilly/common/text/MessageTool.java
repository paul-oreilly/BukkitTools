package com.oreilly.common.text;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


public class MessageTool {
	
	public static String tagOpen( String type ) {
		return "<" + type + "/>";
	}
	
	
	public static String tagClose( String type ) {
		return "</" + type + ">";
	}
	
	
	public static void sendToUser( CommandSender user, String rawText ) {
		// strip away any trailing blank lines (only send newlines when encountering real text)
		// TODO: When dealing with console, strip away newlines before text also.
		int newlineCount = 0;
		for ( String line : rawText.split( "\n" ) ) {
			if ( ChatColor.stripColor( line ).trim().length() == 0 )
				newlineCount++;
			else {
				if ( newlineCount > 0 ) {
					for ( int i = 0; i < newlineCount; i++ )
						user.sendMessage( "" );
					newlineCount = 0;
				}
				user.sendMessage( line );
			}
		}
	}
	
	
	public static String stripTags( String content ) {
		// tags open with "<" or "</", and close with ">"
		String[] openingList = { "<", "</" };
		String current = content;
		for ( String opening : openingList ) {
			String result = "";
			int currentIndex = 0;
			while ( currentIndex < current.length() ) {
				int openingIndex = current.indexOf( opening, currentIndex );
				if ( openingIndex == -1 ) {
					// add the remainder
					result += current.substring( currentIndex );
					break;
				}
				// otherwise, add the previous segment..
				result += current.substring( currentIndex, openingIndex );
				// update the index to the end tag
				currentIndex = current.indexOf( ">", currentIndex + 1 ) + 1;
			}
			current = result;
		}
		return current;
	}
	
}
