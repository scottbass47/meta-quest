package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.VelocityComponent;

public class SimpleVelocitySystem extends IteratingSystem{

	public SimpleVelocitySystem(){
		super(Family.all(VelocityComponent.class, PositionComponent.class).exclude(BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PositionComponent positionComp = Mappers.position.get(entity);
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		
		positionComp.x += velocityComp.dx * deltaTime;
		positionComp.y += velocityComp.dy * deltaTime;
	}
	
}
