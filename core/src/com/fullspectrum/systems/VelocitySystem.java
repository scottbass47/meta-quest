package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.VelocityComponent;

public class VelocitySystem extends IteratingSystem{

	/**
	 * Takes velocity component and applies an impulse to the physics body
	 */
	public VelocitySystem(){
		super(Family.all(VelocityComponent.class, BodyComponent.class, ForceComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		
		velocityComp.dx = body.getLinearVelocity().x;
		velocityComp.dy = body.getLinearVelocity().y;

//		float dx = velocityComp.dx - body.getLinearVelocity().x;
//		float dy = velocityComp.dy - body.getLinearVelocity().y;
//
//		body.applyLinearImpulse(dx, dy, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}
}