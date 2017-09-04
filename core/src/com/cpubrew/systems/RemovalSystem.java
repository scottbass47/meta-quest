package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.cpubrew.component.RemoveComponent;
import com.cpubrew.entity.EntityManager;

public class RemovalSystem extends IteratingSystem{

	public RemovalSystem(World world){
		super(Family.all(RemoveComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		EntityManager.cleanUp(entity);
	}
}
