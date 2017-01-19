package com.fullspectrum.ai;

import com.badlogic.ashley.core.Entity;

public interface AIBehavior {

	public void update(Entity entity, float deltaTime);
	
}
