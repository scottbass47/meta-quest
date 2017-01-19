package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.BehaviorComponent;
import com.fullspectrum.component.Mappers;

public class BehaviorSystem extends IteratingSystem{

	public BehaviorSystem() {
		super(Family.all(BehaviorComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Mappers.behavior.get(entity).behavior.update(deltaTime);
	}

}
