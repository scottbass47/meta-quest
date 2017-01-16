package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.DropMovementComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.ForceComponent.CForce;

public class DropMovementSystem extends IteratingSystem{

	public DropMovementSystem(){
		super(Family.all(DropMovementComponent.class, BodyComponent.class, ForceComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ForceComponent forceComp = Mappers.force.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;

		forceComp.add(CForce.MOVEMENT, body.getLinearVelocity().x * 0.65f, body.getLinearVelocity().y);
		if(Math.abs(body.getLinearVelocity().x) < 1.0f){
			forceComp.add(CForce.MOVEMENT, 0.0f, body.getLinearVelocity().y);
		}
	}
	
}
