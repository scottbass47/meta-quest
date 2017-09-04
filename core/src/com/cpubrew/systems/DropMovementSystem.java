package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.DropMovementComponent;
import com.cpubrew.component.Mappers;

public class DropMovementSystem extends IteratingSystem{

	public DropMovementSystem(){
		super(Family.all(DropMovementComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		
		body.setLinearDamping(0.65f);
		body.setGravityScale(0.75f);
		if(Math.abs(body.getLinearVelocity().x) < 1.0f){
			body.setLinearVelocity(0, body.getLinearVelocity().y);
		}
	}
	
}
