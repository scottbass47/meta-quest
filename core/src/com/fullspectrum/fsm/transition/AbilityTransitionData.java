package com.fullspectrum.fsm.transition;

import com.fullspectrum.entity.AbilityType;

public class AbilityTransitionData implements TransitionData{

	public AbilityType type;
	
	public AbilityTransitionData(AbilityType type) {
		this.type = type;
	}
	
	@Override
	public void reset() {
		
	}
}
