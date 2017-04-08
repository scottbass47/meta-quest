package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.ControlledMovementComponent;
import com.fullspectrum.component.Mappers;

public class ControlledMovementSystem extends IteratingSystem{

	public ControlledMovementSystem() {
		super(Family.all(BodyComponent.class, ControlledMovementComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent bodyComp = Mappers.body.get(entity);
		ControlledMovementComponent controlledMovementComp = Mappers.controlledMovement.get(entity);
		controlledMovementComp.elapsed += deltaTime;
		
		Vector2 expectedSpeed = new Vector2();
		if(controlledMovementComp.movement != null){
			expectedSpeed.set(controlledMovementComp.movement.getVelocity(entity, controlledMovementComp.elapsed, deltaTime));
		}
		
		Vector2 actualSpeed = bodyComp.body.getLinearVelocity();
		bodyComp.body.applyLinearImpulse(expectedSpeed.sub(actualSpeed), bodyComp.body.getWorldCenter(), true);
		
	}
	
}
