package com.oreilly.common.interaction.text;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;


public class EventHook implements Listener {
	
	
	@EventHandler
	public void onChatEvent( PlayerChatEvent event ) {
		Interaction.chatEvent( event );
	}
}
