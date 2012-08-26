package com.oreilly.common.interaction.text.helpers;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralInteractionError;


public interface ChoiceImplementer {
	
	public String takeAction( Interaction interaction, String key ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError;
}
