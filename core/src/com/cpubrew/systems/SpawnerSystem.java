package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.SpawnComponent;
import com.cpubrew.component.SpawnerPoolComponent;
import com.cpubrew.component.SpawnerPoolComponent.SpawnItem;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.entity.EntityManager;

public class SpawnerSystem extends IteratingSystem{

	public SpawnerSystem() {
		super(Family.all(SpawnComponent.class, SpawnerPoolComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(DebugVars.SPAWNERS_DISABLED) return;
		
		SpawnerPoolComponent spawnPool = Mappers.spawnerPool.get(entity);
		float num = MathUtils.random(1.0f);
		
		for(SpawnItem item : spawnPool.getPool()){
			if(num < item.chance){
				// Spawn Enemy
				Body body = Mappers.body.get(entity).body;
				
				EntityManager.addEntity(item.index.create(body.getPosition().x, body.getPosition().y + 1.0f));
				break;
			}
			num -= item.chance;
		}
		
		entity.remove(SpawnComponent.class);
	}
	
}
