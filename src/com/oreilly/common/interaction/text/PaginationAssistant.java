package com.oreilly.common.interaction.text;

import java.util.ArrayList;


public class PaginationAssistant {
	
	public ArrayList< String > pages = new ArrayList< String >();
	public boolean required = false;
	public int currentPage = 1;
	
	
	@SuppressWarnings("unused")
	public PaginationAssistant( String rawInput, int maxLines, String header ) {
		// check if raw input can fit into max lines, and if so, set required to false
		String[] splitInput = rawInput.split( "\n" );
		if ( ( splitInput.length + header.split( "\n" ).length ) <= maxLines ) {
			required = false;
			pages.add( 0, header + rawInput );
		}
		// at this point, we need to allow 2 extra lines for adding "Page x of y",
		// and however many lines the header for each page is
		maxLines -= ( 2 + header.split( "\n" ).length );
		// DEBUG:
		System.out.print( "DEBUG: Max lines value is " + maxLines );
		// find a blank line to split the page on. (Or split at half way, whichever comes first.)
		boolean pagesRemain = true;
		while ( pagesRemain ) {
			int index = ( maxLines >= splitInput.length ) ? splitInput.length - 1 : maxLines;
			if ( index < 0 )
				break;
			else
				while ( ( !splitInput[index].contentEquals( "\n" ) ) & ( index > maxLines / 2 ) )
					index--;
			String page = header;
			for ( int i = 0; i <= index; i++ )
				page += splitInput[i] + "\n";
			pages.add( page );
			String[] newSplitDisplay = new String[0];
			for ( int i = index + 1; i < splitInput.length; i++ )
				newSplitDisplay[newSplitDisplay.length] = splitInput[i];
			splitInput = newSplitDisplay;
		}
		// add a "page x of y" at the bottom of each page
		int currentPage = 1;
		int pageCount = pages.size();
		for ( String value : pages ) {
			value += "\nPage " + currentPage + " of " + pageCount;
			currentPage++;
		}
	}
	
	
	public String getDisplayText() {
		if ( pages == null )
			return null;
		if ( pages.size() == 0 )
			return null;
		if ( currentPage <= 0 )
			currentPage = 1;
		if ( currentPage > pages.size() )
			currentPage = pages.size();
		return pages.get( currentPage - 1 );
	}
	
	
	public boolean processPageCommand( String input ) {
		// return true if a page command, false otherwise.
		input = input.toLowerCase().trim();
		if ( input.startsWith( "page" ) ) {
			input = input.replace( "page", "" ).trim();
			// try to get a page number
			try {
				int pageNum = Integer.parseInt( input );
				currentPage = pageNum;
				return true;
			} catch ( NumberFormatException error ) {
				return true;
			}
		}
		if ( input.contentEquals( "back" ) | input.contentEquals( "previous" ) ) {
			currentPage -= 1;
			return true;
		}
		if ( input.contentEquals( "foward" ) | input.contentEquals( "next" ) ) {
			currentPage += 1;
			return true;
		}
		return false;
	}
	
}
