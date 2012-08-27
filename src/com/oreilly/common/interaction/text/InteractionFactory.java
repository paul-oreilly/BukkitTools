package com.oreilly.common.interaction.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.oreilly.common.interaction.text.Interaction.MessageType;
import com.oreilly.common.interaction.text.formatter.Formatter;
import com.oreilly.common.interaction.text.validator.Validator;
import com.oreilly.common.text.Translater;


public class InteractionFactory {
	
	public Formatter formatter = null;
	public Validator validator = null;
	public ArrayList< InteractionPage > pages = new ArrayList< InteractionPage >();
	public int timeout = 20; // seconds
	public Set< String > exitStrings = new HashSet< String >();
	public Set< String > returnStrings = new HashSet< String >();
	public String nonPlayerExclusionMessage = null;
	public HashMap< String, Object > style = new HashMap< String, Object >();
	public HashMap< MessageType, ArrayList< ChatColor >> messageStyles =
			new HashMap< MessageType, ArrayList< ChatColor >>();
	public Translater translator = null;
	public String defaultTranslation = null;
	
	
	public InteractionFactory() {
	}
	
	
	public Interaction buildInteraction( CommandSender sender ) {
		if ( nonPlayerExclusionMessage != null )
			if ( !( sender instanceof Player ) ) {
				sender.sendMessage( nonPlayerExclusionMessage );
				return null;
			}
		ArrayList< InteractionPage > pagesCopy = new ArrayList< InteractionPage >();
		pagesCopy.addAll( pages );
		return new Interaction( sender )
				.withFormatter( formatter )
				.withValidator( validator )
				.withTimeout( timeout )
				.withExitStrings( exitStrings )
				.withReturnStrings( returnStrings )
				.withPages( pagesCopy )
				.withStyles( style )
				.withMessageStyles( messageStyles )
				.withTranslator( translator, defaultTranslation );
	}
	
	
	// chained init methods
	
	public InteractionFactory withMessageStyle( MessageType type, ChatColor... style ) {
		ArrayList< ChatColor > styleList = new ArrayList< ChatColor >();
		for ( ChatColor c : style )
			styleList.add( c );
		messageStyles.put( type, styleList );
		return this;
	}
	
	
	public InteractionFactory withMessageStyles( HashMap< MessageType, ArrayList< ChatColor >> styles ) {
		for ( MessageType type : styles.keySet() ) {
			ArrayList< ChatColor > source = styles.get( type );
			ArrayList< ChatColor > list = new ArrayList< ChatColor >();
			if ( source != null ) {
				for ( ChatColor c : source )
					list.add( c );
				messageStyles.put( type, list );
			}
		}
		return this;
	}
	
	
	public InteractionFactory withExitSequence( String... sequence ) {
		for ( String item : sequence )
			exitStrings.add( item );
		return this;
	}
	
	
	public InteractionFactory withReturnSequence( String... sequence ) {
		for ( String item : sequence )
			returnStrings.add( item );
		return this;
	}
	
	
	public InteractionFactory withFormatter( Formatter formatter ) {
		if ( this.formatter == null )
			this.formatter = formatter;
		else
			this.formatter.chain( formatter );
		return this;
	}
	
	
	public InteractionFactory withValidator( Validator validator ) {
		if ( this.validator == null )
			this.validator = validator;
		else
			this.validator.chain( validator );
		return this;
	}
	
	
	public InteractionFactory withReplacementFormatter( Formatter formatter ) {
		this.formatter = formatter;
		return this;
	}
	
	
	public InteractionFactory withReplacementValidator( Validator validator ) {
		this.validator = validator;
		return this;
	}
	
	
	public InteractionFactory withTimeout( int timeout ) {
		this.timeout = timeout;
		return this;
	}
	
	
	public InteractionFactory thatExcludesNonPlayersWithMessage( String msg ) {
		this.nonPlayerExclusionMessage = msg;
		return this;
	}
	
	
	public InteractionFactory withPages( ArrayList< InteractionPage > pageList ) {
		this.pages = pageList;
		return this;
	}
	
	
	public InteractionFactory withPages( InteractionPage... pageList ) {
		for ( InteractionPage page : pageList )
			pages.add( page );
		return this;
	}
	
	
	public InteractionFactory withStyle( String key, Object style ) {
		this.style.put( key, style );
		return this;
	}
	
	public InteractionFactory withTranslation( Translater translator, String defaultTranslation ) {
		this.defaultTranslation = defaultTranslation;
		this.translator = translator;
		return this;
	}
	
}
