package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.Mappers;

public class ForceSystem extends IteratingSystem{

	public ForceSystem(){
		super(Family.all(ForceComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ForceComponent forceComp = Mappers.force.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		Body body = bodyComp.body;
		body.applyForceToCenter(forceComp.fx, forceComp.fy, true);
		body.setLinearDamping(0.8f);
		entity.remove(ForceComponent.class);
	}
	
}
