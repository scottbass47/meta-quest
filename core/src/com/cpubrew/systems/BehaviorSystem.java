package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.cpubrew.component.BehaviorComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.debug.DebugVars;

public class BehaviorSystem extends IteratingSystem{

	public BehaviorSystem() {
		super(Family.all(BehaviorComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(DebugVars.AI_DISABLED) return;
		Mappers.behavior.get(entity).behavior.update(entity, deltaTime);
	}

}
