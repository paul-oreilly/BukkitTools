package com.oreilly.common.io;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Locations {
	
	static public Location fromString( String data, String errorLocation, Logger log ) {
		// assumed to have used Location.toString to get data, which resuts in matched sets like
		// Location{world=[value] pitch=[value] ...} etc.
		if ( data == null )
			return null;
		// check we have a location string
		if ( ! data.toLowerCase().trim().startsWith("location{")) {
			log.warning("Location does appear correctly formatted. Expected \"Location{world=[value],x=[value]...}\"");
			return null;
		}
		data = data.replace( "location{", "").replace("}", "");
		// we must have world, x, y, and z to make a location - so at least 4 bits of data
		String[] splitString = data.split(",");
		if ( splitString.length < 4 ) {
			log.warning("Unable to determine location from " + data + ". Expecting at least 4 pairs of information");
			return null;
		}
		World world = null;
		Float x = null;
		Float y = null;
		Float z = null;
		Float pitch = null;
		Float yaw = null;
		for ( String item : splitString ) {
			item = item.trim();
			if ( item.startsWith("world=")) {
				String worldName = item.replace("world=", "").trim();
				world = Bukkit.getWorld( worldName );
				continue;
			}
			if ( item.startsWith("x=")) {
				x = Float.valueOf(item.replace("x=", "").trim());
				continue;
			}
			if ( item.startsWith("y=")) {
				y = Float.valueOf(item.replace("y=", "").trim());
				continue;
			}
			if ( item.startsWith("z=")) {
				z = Float.valueOf(item.replace("z=", "").trim());
				continue;
			}
			if ( item.startsWith("pitch=")) {
				pitch = Float.valueOf(item.replace("pitch=", "").trim());
				continue;
			}
			if ( item.startsWith("yaw=")) {
				yaw = Float.valueOf(item.replace("yaw=", "").trim());
				continue;
			}
		}
		// check we have the basic required information
		if ( world == null ) {
			log.warning("Unable to find world for location in " + errorLocation + ", from string \"" + data +"\"" );
			return null;
		}
		if ( x == null ) {
			log.warning("Unable to find or parse x in " + errorLocation + ", within string \"" + data +"\"" );
			return null;
		}
		if ( y == null ) {
			log.warning("Unable to find or parse y in " + errorLocation + ", within string \"" + data +"\"" );
			return null;
		}	
		if ( z == null ) {
			log.warning("Unable to find or parse z in " + errorLocation + ", within string \"" + data +"\"" );
			return null;
		}
		// we have enough for a basic location
		Location result = new Location( world, x, y, z );
		// add other data if we have it
		if ( yaw != null )
			result.setYaw( yaw );
		if ( pitch != null )
			result.setPitch( pitch );
		// all done.
		return result;
	}
	
}
