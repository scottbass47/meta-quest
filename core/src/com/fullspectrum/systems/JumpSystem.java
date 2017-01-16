package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.Mappers;

public class JumpSystem extends IteratingSystem{

	public JumpSystem(){
		super(Family.all(JumpComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Body body = Mappers.body.get(entity).body;
		JumpComponent jumpComp = Mappers.jump.get(entity);
		
//		body.applyForceToCenter(0, jumpComp.force, true);
		float velChange = jumpComp.maxForce * jumpComp.multiplier - body.getLinearVelocity().y;
		float impulse = body.getMass() * velChange;
		body.applyLinearImpulse(0, impulse, body.getWorldCenter().x, body.getWorldCenter().y, true);
		entity.remove(JumpComponent.class);
	}
	
}
