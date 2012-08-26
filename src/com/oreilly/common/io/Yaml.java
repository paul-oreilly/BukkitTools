package com.oreilly.common.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;


public class Yaml {
	
	public static List< YamlConfiguration > loadYamlDirectory( File file, Logger errorLog ) {
		LinkedList< YamlConfiguration > result = new LinkedList< YamlConfiguration >();
		if ( !file.exists() ) {
			file.mkdirs();
			errorLog.info( "Creating directory " + file.getAbsolutePath() + " as it does not exist" );
			return result;
		}
		if ( !file.isDirectory() ) {
			errorLog.warning( "Call to loadYamlDirectory was made, but " + file.getAbsolutePath() +
					" is not a directory" );
			return result;
		}
		for ( File f : file.listFiles() ) {
			// only yml files.. TODO: Check case factors
			if ( !f.getName().endsWith( ".yml" ) )
				continue;
			YamlConfiguration config = loadYamlFile( f, errorLog );
			if ( config != null )
				result.add( config );
		}
		return result;
	}
	
	
	public static YamlConfiguration loadYamlFile( File file, Logger errorLog ) {
		if ( !file.exists() ) {
			try {
				if ( errorLog != null )
					errorLog.info( "File " + file.getAbsolutePath() + " does not exist, creating.." );
				File parent = file.getParentFile();
				if ( file.getParentFile().exists() == false ) {
					parent.mkdirs();
					if ( errorLog != null )
						errorLog.info( "Directory " + parent.getAbsolutePath() + " does not exist, creating.." );
				}
				file.createNewFile();
			} catch ( IOException e ) {
				if ( errorLog != null )
					errorLog.warning( "IO Error while trying to create " + file.getAbsolutePath() );
				e.printStackTrace();
			}
		}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load( file );
		} catch ( FileNotFoundException e ) {
			if ( errorLog != null )
				errorLog.warning( "IO Error (File not found) while trying to load " + file.getAbsolutePath() );
			e.printStackTrace();
		} catch ( IOException e ) {
			if ( errorLog != null )
				errorLog.warning( "IO Error (IO Exception) while trying to load " + file.getAbsolutePath() );
			e.printStackTrace();
		} catch ( InvalidConfigurationException e ) {
			if ( errorLog != null )
				errorLog.warning( "Invlaid yaml file encountered while trying to load " + file.getAbsolutePath() );
			e.printStackTrace();
		}
		return config;
	}
}
