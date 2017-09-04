package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.NoMovementComponent;

public class NoMovementSystem extends IteratingSystem {

	public NoMovementSystem() {
		super(Family.all(BodyComponent.class, NoMovementComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent bodyComp = Mappers.body.get(entity);
		NoMovementComponent noComp = Mappers.noMovement.get(entity);
		
		Vector2 actual = bodyComp.body.getLinearVelocity();
		Vector2 wanted = new Vector2(0.0f, noComp.ignoreGravity ? 0.0f : actual.y);
		bodyComp.body.applyLinearImpulse(wanted.sub(actual), bodyComp.body.getWorldCenter(), true);
	}
	
}
