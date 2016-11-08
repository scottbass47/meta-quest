package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.DropSpawnComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.DropFactory;

public class DropSpawnSystem extends IteratingSystem{

	public DropSpawnSystem(){
		super(Family.all(DropSpawnComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		DropSpawnComponent dropSpawnComp = Mappers.dropSpawn.get(entity);
		
		switch(dropSpawnComp.type){
		case COIN:
			DropFactory.spawnCoins(entity);
			entity.remove(DropSpawnComponent.class);
			break;
		default:
			break;
		}
		
	}
	
}
