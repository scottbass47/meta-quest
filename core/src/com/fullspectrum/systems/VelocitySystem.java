package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.VelocityComponent;

public class VelocitySystem extends IteratingSystem{

	public VelocitySystem(){
		super(Family.all(DirectionComponent.class, SpeedComponent.class, VelocityComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		DirectionComponent directionComp = Mappers.direction.get(entity);
		SpeedComponent speedComp = Mappers.speed.get(entity);
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		
		velocityComp.x = speedComp.maxSpeed * speedComp.multiplier * directionComp.direction.getDirection();
	}
	
}
