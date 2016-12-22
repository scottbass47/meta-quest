package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.WallComponent;

public class WallSlideSystem extends IteratingSystem{

	public WallSlideSystem(){
		super(Family.all(WallComponent.class, CollisionComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		FacingComponent facingComp = Mappers.facing.get(entity);
		CollisionComponent collisionComp = Mappers.collision.get(entity);
		facingComp.facingRight = collisionComp.onLeftWall();
	}
	
}
