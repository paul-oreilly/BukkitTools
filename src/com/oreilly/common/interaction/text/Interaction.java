package com.oreilly.common.interaction.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralDisplayError;
import com.oreilly.common.interaction.text.error.InterfaceDependencyError;
import com.oreilly.common.interaction.text.error.ValidationFailedError;
import com.oreilly.common.interaction.text.formatter.Formatter;
import com.oreilly.common.interaction.text.validator.Validator;


public class Interaction {
	
	static public final String STYLE_ERROR_COLOR = "errorColor";
	static public ChatColor defaultErrorColor = ChatColor.DARK_RED;
	
	static public HashMap< CommandSender, Interaction > currentInteractions = new HashMap< CommandSender, Interaction >();
	static private boolean eventListenerRegisterd = false;
	
	public Formatter formatter = null;
	public Validator validator = null;
	public ArrayList< InteractionPage > pages = new ArrayList< InteractionPage >();
	public ArrayList< InteractionPage > history = new ArrayList< InteractionPage >();
	public int timeout = 20; // seconds
	public CommandSender user = null;
	public Set< String > exitStrings = new HashSet< String >();
	public Set< String > returnStrings = new HashSet< String >();
	public List< String > chatBuffer = new ArrayList< String >();
	public HashMap< String, Object > style = new HashMap< String, Object >();
	
	// called if current page is still 'holding' the player interaction
	public boolean pageWaitingForInput = false;
	
	// for holding any data required
	public HashMap< String, Object > context = new HashMap< String, Object >();
	
	// the current page 
	public InteractionPage currentPage = null;
	
	
	static public boolean registerEventListener( Plugin plugin ) {
		if ( eventListenerRegisterd )
			return false;
		else {
			plugin.getServer().getPluginManager().registerEvents( new EventHook(), plugin );
			return true;
		}
	}
	
	
	// returns true if the message was for an interaction, and was passed on.
	static public boolean chatEvent( PlayerChatEvent event ) {
		// for every active interaction, hold the chat event in a buffer for later delivery
		Set< Player > recipients = event.getRecipients();
		for ( Interaction interaction : currentInteractions.values() ) {
			if ( recipients.remove( interaction.user ) )
				if ( interaction.user != event.getPlayer() )
					interaction.chatBuffer.add( event.getPlayer().getDisplayName() + ":" + event.getMessage() );
		}
		// see if the player who talked is currently interacting
		Interaction interaction = currentInteractions.get( event.getPlayer() );
		if ( interaction == null )
			return false;
		// pass the message to the interaction, and cancel the event
		interaction.acceptInput( event.getMessage() );
		event.setCancelled( true );
		return true;
	}
	
	
	public Interaction( CommandSender user ) {
		this();
		this.user = user;
		currentInteractions.put( user, this );
	}
	
	
	protected Interaction() {
		
	}
	
	
	public void interactionComplete() {
		sendQueuedMessages();
		currentInteractions.remove( user );
		formatter = null;
		validator = null;
		pages = null;
		exitStrings = null;
		user = null;
		chatBuffer = null;
	}
	
	
	public void acceptInput( String input ) {
		String universalInput = input.toLowerCase().trim();
		// exit the conversation if input matches one of the exit strings
		if ( exitStrings.contains( universalInput ) ) {
			interactionComplete();
			return;
		}
		// return to the previous page, if input matches one of the return strings
		if ( returnStrings.contains( universalInput ) ) {
			pages.add( 0, currentPage );
			if ( history.size() > 1 ) {
				currentPage = history.remove( history.size() - 1 );
				display();
			}
			return;
		}
		// exit the conversation if there is no current page
		if ( currentPage == null ) {
			interactionComplete();
			return;
		}
		try {
			// validate input
			Object validatedInput = input;
			if ( validator != null )
				validatedInput = validator.startValidation( validatedInput, currentPage );
			if ( currentPage.validator != null )
				validatedInput = currentPage.validator.startValidation( validatedInput, currentPage );
			// pass input to the page to take action on
			String reply = currentPage.acceptValidatedInput( this, validatedInput );
			if ( reply != null )
				if ( !reply.isEmpty() )
					user.sendMessage( reply.split( "\n" ) );
			// progress to the next page.. unless the current page has a 'lock' on interaction
			if ( pageWaitingForInput ) {
				pageWaitingForInput = false;
			} else {
				history.add( currentPage );
				if ( pages.size() > 0 )
					currentPage = pages.remove( 0 );
				else
					currentPage = null;
			}
			// show the next / repeat page
			display();
		} catch ( ValidationFailedError error ) {
			// display the current page again
			display();
			// show what went wrong last time
			if ( currentPage.validationFailedMessage != null )
				sendValidationError( currentPage.validationFailedMessage, input );
			else
				sendValidationError( error.message, input );
		} catch ( InterfaceDependencyError error ) {
			user.sendMessage( "Internal error - unmet interface dependency " + error.interfaceRequired );
		} catch ( ContextDataRequired error ) {
			// show the previous page, then an error about context
			if ( history.size() > 1 ) {
				currentPage = history.remove( history.size() - 1 );
				display();
			}
			user.sendMessage( ChatColor.DARK_RED + "ERROR: Unable to display page, as required context " +
					error.key + "(" + error.classType + ") does not exist" );
		} catch ( GeneralDisplayError error ) {
			if ( history.size() > 1 ) {
				currentPage = history.remove( history.size() - 1 );
				display();
			}
			user.sendMessage( ChatColor.DARK_RED + "ERROR: " + error.reason );
		}
	}
	
	
	protected void sendValidationError( String message, String input ) {
		// get the colour for errors
		Object rawStyle = style.get( Interaction.STYLE_ERROR_COLOR );
		String errorColor = null;
		if ( rawStyle instanceof ChatColor )
			errorColor = ( (ChatColor)rawStyle ).toString();
		else
			errorColor = defaultErrorColor.toString();
		// replace variables that may be in the message
		message = errorColor + message.replace( "%input", input );
		// send the message to the player
		user.sendMessage( message.split( "\n" ) );
	}
	
	
	public void begin() {
		if ( pages.size() == 0 ) {
			user.sendMessage( "DEBUG: No pages exist for this interaction" );
			return;
		}
		currentPage = pages.remove( 0 );
		display();
	}
	
	
	protected void display() {
		if ( currentPage == null ) {
			interactionComplete();
			return;
		}
		currentPage.style.putAll( style );
		try {
			String currentDisplay = currentPage.getDisplayText( this );
			if ( currentPage.formatter != null )
				currentDisplay = currentPage.formatter.startFormatting( currentDisplay, currentPage, this );
			if ( formatter != null )
				currentDisplay = formatter.startFormatting( currentDisplay, currentPage, this );
			// send the display to the user
			user.sendMessage( currentDisplay.split( "\n" ) );
		} catch ( ContextDataRequired error ) {
			// show the previous page, then an error about context
			if ( history.size() > 1 ) {
				currentPage = history.remove( history.size() - 1 );
				display();
			}
			user.sendMessage( ChatColor.DARK_RED + "ERROR: Unable to display next page, as required context " +
					error.key + "(" + error.classType + ") does not exist" );
		} catch ( GeneralDisplayError error ) {
			if ( history.size() > 1 ) {
				currentPage = history.remove( history.size() - 1 );
				display();
			}
			user.sendMessage( ChatColor.DARK_RED + "ERROR: " + error.reason );
		}
	}
	
	
	// methods to make pages easier
	
	public Interaction nextPage( InteractionPage page ) {
		pages.add( 0, page );
		return this;
	}
	
	
	public Interaction addPages( InteractionPage... pageList ) {
		int i = pageList.length - 1;
		while ( i >= 0 ) {
			pages.add( 0, pageList[i] );
			// add any style overwrites
			pageList[i].withStyles( style );
			i--;
		}
		return this;
	}
	
	
	// chained init methods
	
	public Interaction withFormatter( Formatter formatter ) {
		if ( this.formatter == null )
			this.formatter = formatter;
		else
			this.formatter.chain( formatter );
		return this;
	}
	
	
	public Interaction withValidator( Validator validator ) {
		if ( this.validator == null )
			this.validator = validator;
		else
			this.validator.chain( validator );
		return this;
	}
	
	
	public Interaction withReplacementFormatter( Formatter formatter ) {
		this.formatter = formatter;
		return this;
	}
	
	
	public Interaction withReplacementValidator( Validator validator ) {
		this.validator = validator;
		return this;
	}
	
	
	public Interaction withExitStrings( String... sequence ) {
		for ( String item : sequence )
			exitStrings.add( item );
		return this;
	}
	
	
	public Interaction withExitStrings( Set< String > set ) {
		exitStrings.addAll( set );
		return this;
	}
	
	
	public Interaction withReturnStrings( String... sequence ) {
		for ( String item : sequence )
			returnStrings.add( item );
		return this;
	}
	
	
	public Interaction withReturnStrings( Set< String > set ) {
		returnStrings.addAll( set );
		return this;
	}
	
	
	public Interaction withPages( ArrayList< InteractionPage > pages ) {
		this.pages = pages;
		return this;
	}
	
	
	public Interaction withPages( InteractionPage... pageList ) {
		addPages( pageList );
		return this;
	}
	
	
	public Interaction withTimeout( int timeout ) {
		this.timeout = timeout;
		return this;
	}
	
	
	public Interaction withStyle( String key, Object style ) {
		this.style.put( key, style );
		return this;
	}
	
	
	public Interaction withStyles( HashMap< String, Object > source ) {
		style.putAll( source );
		return this;
	}
	
	
	// Helper functions...
	
	public < T > T getContextData( Class< T > tClass, Interaction interaction, String key ) throws ContextDataRequired {
		return getContextData( tClass, interaction, key, false );
	}
	
	
	@SuppressWarnings("unchecked")
	public < T > T getContextData( Class< T > tClass, Interaction interaction, String key, boolean throwError )
			throws ContextDataRequired {
		Object obj = interaction.context.get( key );
		if ( obj == null ) {
			if ( throwError )
				throw new ContextDataRequired( key, tClass );
			else
				return null;
		}
		if ( obj.getClass().isAssignableFrom( tClass ) )
			return (T)obj;
		else {
			if ( throwError )
				throw new ContextDataRequired( key, tClass );
			else
				return null;
		}
	}
	
	
	// internal methods
	
	protected void sendQueuedMessages() {
		for ( String message : chatBuffer )
			user.sendMessage( message.split( "\n" ) );
	}
	
}
