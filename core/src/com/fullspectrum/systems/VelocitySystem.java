package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.input.Actions;

public class VelocitySystem extends IteratingSystem{

	public VelocitySystem(){
		super(Family.all(DirectionComponent.class, SpeedComponent.class, VelocityComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		DirectionComponent directionComp = Mappers.direction.get(entity);
		SpeedComponent speedComp = Mappers.speed.get(entity);
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);
		
		if(inputComp != null){
			speedComp.multiplier = Math.abs(inputComp.input.getValue(Actions.MOVE_LEFT) - inputComp.input.getValue(Actions.MOVE_RIGHT));
		}
		
		velocityComp.dx = speedComp.maxSpeed * speedComp.multiplier * directionComp.direction.getDirection();
	}
	
}
