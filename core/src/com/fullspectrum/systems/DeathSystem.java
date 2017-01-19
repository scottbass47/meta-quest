package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;

public class DeathSystem extends IteratingSystem {

	public DeathSystem() {
		super(Family.all(DeathComponent.class).get());
	}

	protected void processEntity(Entity entity, float deltaTime) {
		HealthComponent healthComp = Mappers.heatlh.get(entity);
		DeathComponent deathComp = Mappers.death.get(entity);
		
		if(healthComp != null && healthComp.health <= 0.0f){
			deathComp.triggerDeath();
		}
		
		if (deathComp.shouldDie()) {
			deathComp.onDeath.onDeath(entity);
			deathComp.makeDead();
		}
	}
}