package com.fullspectrum.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.RemoveComponent;

public class RemovalSystem extends IteratingSystem{

	private World world;
	
	@SuppressWarnings("unchecked")
	public RemovalSystem(World world){
		super(Family.all(RemoveComponent.class).get());
		this.world = world;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		if(bodyComp != null && bodyComp.body != null){
			world.destroyBody(bodyComp.body);
		}
		for(Component c : entity.getComponents()){
			entity.remove(c.getClass());
		}
		getEngine().removeEntity(entity);
	}
}
