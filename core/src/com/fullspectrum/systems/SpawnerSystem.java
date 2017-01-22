package com.fullspectrum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpawnComponent;
import com.fullspectrum.component.SpawnerPoolComponent;
import com.fullspectrum.component.SpawnerPoolComponent.SpawnItem;
import com.fullspectrum.level.Level;

public class SpawnerSystem extends IteratingSystem{

	public SpawnerSystem() {
		super(Family.all(SpawnComponent.class, SpawnerPoolComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SpawnerPoolComponent spawnPool = Mappers.spawnerPool.get(entity);
		float num = MathUtils.random(1.0f);
		
		for(SpawnItem item : spawnPool.getPool()){
			if(num < item.chance){
				// Spawn Enemy
				Engine engine = Mappers.engine.get(entity).engine;
				World world = Mappers.world.get(entity).world;
				Level level = Mappers.level.get(entity).level;
				Body body = Mappers.body.get(entity).body;
				
				engine.addEntity(item.index.create(engine, world, level, body.getPosition().x, body.getPosition().y + 1.0f));
				break;
			}
			num -= item.chance;
		}
		
		entity.remove(SpawnComponent.class);
	}
	
}
