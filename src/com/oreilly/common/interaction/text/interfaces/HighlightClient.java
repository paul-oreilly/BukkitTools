package com.oreilly.common.interaction.text.interfaces;

import java.util.HashMap;
import java.util.Iterator;


public interface HighlightClient {
	
	// maped by: (highlight type) -> (list of words)
	public HashMap< String, Iterator< String >> getHighlightList();
}
