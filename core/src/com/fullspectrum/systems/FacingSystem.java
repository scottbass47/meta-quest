package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.VelocityComponent;

public class FacingSystem extends IteratingSystem{

	public FacingSystem(){
		super(Family.all(FacingComponent.class, VelocityComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		FacingComponent facingComp = Mappers.facing.get(entity);
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		float dx = velocityComp.x;
		facingComp.facingRight = dx > 0 || dx < 0 ? dx > 0 : facingComp.facingRight;
	}	
}
