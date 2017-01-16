package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.Mappers;

public class JumpSystem extends IteratingSystem{

	public JumpSystem(){
		super(Family.all(JumpComponent.class, ForceComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ForceComponent forceComp = Mappers.force.get(entity);
		JumpComponent jumpComp = Mappers.jump.get(entity);
		
		forceComp.add(0.0f, jumpComp.maxForce * jumpComp.multiplier);
		entity.remove(JumpComponent.class);
	}
	
}
