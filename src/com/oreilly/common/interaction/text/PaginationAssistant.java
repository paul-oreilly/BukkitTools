package com.oreilly.common.interaction.text;

import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;


public class PaginationAssistant {
	
	public static final boolean DEBUG = true;
	
	public LinkedList< String > pages = new LinkedList< String >();
	public boolean required = false;
	public int currentPage = 1;
	
	
	public PaginationAssistant( String rawInput, int maxLines, String header ) {
		// check if raw input can fit into max lines, and if so, set required to false
		String[] splitInput = rawInput.split( "\n" );
		if ( ( splitInput.length + header.split( "\n" ).length ) <= maxLines ) {
			required = false;
			pages.add( 0, header + rawInput );
			return;
		}
		// at this point, we need to allow 2 extra lines for adding "Page x of y",
		// and however many lines the header for each page is
		maxLines -= ( 2 + header.split( "\n" ).length );
		// DEBUG:
		System.out.print( "DEBUG: Max lines value is " + maxLines );
		// find a blank line to split the page on. (Or split at half way, whichever comes first.)
		LinkedList< String > queue = new LinkedList< String >();
		for ( String line : splitInput )
			queue.add( line );
		while ( queue.size() > 0 ) {
			int index = ( maxLines >= queue.size() ) ? queue.size() - 1 : maxLines;
			while ( ( !queue.get( index ).contentEquals( "\n" ) ) & ( index > maxLines / 2 ) )
				index--;
			String page = header;
			for ( int i = 0; i <= index; i++ )
				page += queue.remove() + "\n";
			pages.add( page );
			if ( DEBUG ) {
				System.out.println( "com.oreilly.common..PaginationAssistant: New page created:\n" +
						page + "\nQueue is now:\n" + StringUtils.join( queue.iterator(), "\n" ) );
			}
		}
		// add a "page x of y" at the bottom of each page
		int currentPage = 1;
		queue = pages;
		pages = new LinkedList< String >();
		for ( String value : queue ) {
			pages.add( value + "\nPage " + currentPage + " of " + queue.size() );
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
		if ( DEBUG )
			System.out.println( "Getting display text for page " + currentPage + ":\n" + pages.get( currentPage - 1 ) );
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
