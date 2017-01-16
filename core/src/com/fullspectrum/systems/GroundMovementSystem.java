package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.ForceComponent.CForce;
import com.fullspectrum.input.Actions;

public class GroundMovementSystem extends IteratingSystem{

//	public GroundMovementSystem(){
//		super(Family.all(GroundMovementComponent.class, BodyComponent.class, VelocityComponent.class).get());
//	}
//
//	@Override
//	protected void processEntity(Entity entity, float deltaTime) {
//		Body body = Mappers.body.get(entity).body;
//		VelocityComponent velocityComp = Mappers.velocity.get(entity);
//		float velChange = velocityComp.dx - body.getLinearVelocity().x;
//		float impulse = body.getMass() * velChange;
//		body.applyLinearImpulse(impulse, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
//	}
	
	public GroundMovementSystem(){
		super(Family.all(GroundMovementComponent.class, ForceComponent.class, DirectionComponent.class, SpeedComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ForceComponent forceComp = Mappers.force.get(entity);
		DirectionComponent directionComp = Mappers.direction.get(entity);
		SpeedComponent speedComp = Mappers.speed.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);
		
		if(inputComp != null){
			speedComp.multiplier = Math.abs(inputComp.input.getValue(Actions.MOVE_LEFT) - inputComp.input.getValue(Actions.MOVE_RIGHT));
		}
		
		float vx = speedComp.multiplier * speedComp.maxSpeed * directionComp.direction.getDirection();
		forceComp.add(CForce.MOVEMENT, vx, 0);
	}
	
}
