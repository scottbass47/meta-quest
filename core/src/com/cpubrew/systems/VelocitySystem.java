package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.VelocityComponent;

public class VelocitySystem extends IteratingSystem{

	public VelocitySystem(){
		super(Family.all(VelocityComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);

		velocityComp.dx = bodyComp.body.getLinearVelocity().x;
		velocityComp.dy = bodyComp.body.getLinearVelocity().y;
	}
	
}
