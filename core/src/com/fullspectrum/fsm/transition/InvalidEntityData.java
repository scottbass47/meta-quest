package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;

public class InvalidEntityData implements TransitionData{

	public Entity toFollow;
	
	public InvalidEntityData(Entity toFollow){
		this.toFollow = toFollow;
	}
	
	@Override
	public void reset() {
	
	}
	
}
