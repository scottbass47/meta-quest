package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;

public class DeathSystem extends IteratingSystem{

	public DeathSystem(){
		super(Family.all(HealthComponent.class).get());
	}
	
	protected void processEntity(Entity entity, float deltaTime) {
		HealthComponent healthComp = Mappers.heatlh.get(entity);
		if(healthComp.health <= 0){
			DeathComponent deathComp = Mappers.death.get(entity);
			if(!deathComp.triggered){
				deathComp.onDeath.onDeath(entity);
				deathComp.triggered = true;
			}
		}
	}
}