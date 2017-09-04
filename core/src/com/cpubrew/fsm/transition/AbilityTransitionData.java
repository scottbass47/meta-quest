package com.cpubrew.fsm.transition;

import com.cpubrew.ability.AbilityType;

public class AbilityTransitionData implements TransitionData{

	public AbilityType type;
	
	public AbilityTransitionData(AbilityType type) {
		this.type = type;
	}
	
	@Override
	public void reset() {
		
	}
}
