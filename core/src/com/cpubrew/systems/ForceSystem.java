package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.ForceComponent;
import com.cpubrew.component.Mappers;

public class ForceSystem extends IteratingSystem{

	public ForceSystem(){
		super(Family.all(ForceComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ForceComponent forceComp = Mappers.force.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		Body body = bodyComp.body;
		body.applyLinearImpulse(forceComp.fx, forceComp.fy, body.getWorldCenter().x, body.getWorldCenter().y, true);
		entity.remove(ForceComponent.class);
	}
	
}
