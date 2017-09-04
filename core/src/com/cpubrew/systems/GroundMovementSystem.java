package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.DirectionComponent;
import com.cpubrew.component.EaseComponent;
import com.cpubrew.component.GroundMovementComponent;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.SpeedComponent;
import com.cpubrew.input.Actions;

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
		
		EaseComponent easeComp = Mappers.ease.get(entity);
		
		if(easeComp != null) {
			float prevX = easeComp.prevX;
			float accel = easeComp.accel * deltaTime;
			vx = prevX < vx ? prevX + accel : prevX - accel;
			easeComp.prevX = vx;
		}
		
		
		bodyComp.body.applyLinearImpulse(vx - bodyComp.body.getLinearVelocity().x, 0, bodyComp.body.getWorldCenter().x, bodyComp.body.getWorldCenter().y, true);
	}
	
}
