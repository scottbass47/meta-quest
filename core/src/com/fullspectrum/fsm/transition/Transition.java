package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;

public interface Transition {

	public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime);
	public boolean allowMultiple();
	
}
