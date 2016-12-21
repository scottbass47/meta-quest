package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.AirMovementComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.VelocityComponent;

public class AirMovementSystem extends IteratingSystem{

	public AirMovementSystem(){
		super(Family.all(AirMovementComponent.class, BodyComponent.class, VelocityComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Body body = Mappers.body.get(entity).body;
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		float velChange = velocityComp.x - body.getLinearVelocity().x;
		float impulse = body.getMass() * velChange;
		body.applyLinearImpulse(impulse * deltaTime * 10, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}
	
}
