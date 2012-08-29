package com.oreilly.common.text;

import java.util.HashMap;


// replaces variables {%variable} with information from a provided lookup table
// recursive - contents can include {%variable}'s of it's own, for translations etc.
// substitues <tag>text</tag> to new tag types (eg colors)
// highlights text based on color tags

public class VariableTool {
	
	static public String variable( String variableName ) {
		return "{%" + variableName + "}";
	}
	
	
	static public String applyVariables( HashMap< String, Object > variables, String text ) {
		// variable results can contain variables themselves (eg translations)
		// so we keep running over the string until no variables are left.
		
		String current = text;
		String result = "";
		int currentPosition = 0;
		int variableIndex = 0;
		
		while ( current.indexOf( "{%" ) != -1 ) {
			while ( currentPosition < current.length() ) {
				variableIndex = current.indexOf( "{%", currentPosition );
				if ( variableIndex == -1 ) {
					// no more variables, so copy the last part of the string over
					result += current.substring( currentPosition );
					break;
				}
				// add the text before the variable
				result += current.substring( currentPosition, variableIndex );
				// otherwise, we have found a variable...
				int variableTagEnds = current.indexOf( "}", variableIndex );
				String variableString = current.substring( variableIndex + 2, variableTagEnds );
				// see if get a direct match
				Object value = variables.get( variableString );
				if ( value != null )
					result += value.toString();
				else {
					// search while ignoring case
					variableString = variableString.trim();
					for ( String key : variables.keySet() ) {
						if ( key.equalsIgnoreCase( variableString ) ) {
							value = variables.get( key );
							break;
						}
					}
					if ( value != null )
						result += value.toString();
				}
				currentPosition = variableTagEnds + 1;
			}
			current = result;
			result = "";
			currentPosition = 0;
		}
		return current;
	}
	
}
