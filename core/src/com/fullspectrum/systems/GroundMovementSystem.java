package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.input.Actions;

public class GroundMovementSystem extends IteratingSystem{

	public GroundMovementSystem(){
		super(Family.all(GroundMovementComponent.class, DirectionComponent.class, SpeedComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent bodyComp = Mappers.body.get(entity);
		DirectionComponent directionComp = Mappers.direction.get(entity);
		SpeedComponent speedComp = Mappers.speed.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);

		if(inputComp != null){
			speedComp.multiplier = Math.abs(inputComp.input.getValue(Actions.MOVE_LEFT) - inputComp.input.getValue(Actions.MOVE_RIGHT));
		}
		float vx = speedComp.multiplier * speedComp.maxSpeed * directionComp.direction.getDirection();
		bodyComp.body.applyLinearImpulse(vx - bodyComp.body.getLinearVelocity().x, 0, bodyComp.body.getWorldCenter().x, bodyComp.body.getWorldCenter().y, true);
	}
	
}
