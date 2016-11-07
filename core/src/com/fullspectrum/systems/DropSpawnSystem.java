package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.CoinSpawnComponent;
import com.fullspectrum.entity.DropFactory;

public class DropSpawnSystem extends IteratingSystem{

	public DropSpawnSystem(){
		super(Family.all(CoinSpawnComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		DropFactory.spawnCoins(entity);
		entity.remove(CoinSpawnComponent.class);
	}
	
}
