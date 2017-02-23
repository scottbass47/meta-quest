package com.fullspectrum.fsm.transition;

import com.fullspectrum.ability.AbilityType;

public class AbilityTransitionData implements TransitionData{

	public AbilityType type;
	
	public AbilityTransitionData(AbilityType type) {
		this.type = type;
	}
	
	@Override
	public void reset() {
		
	}
}
