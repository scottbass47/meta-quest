package com.fullspectrum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.component.BTComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.debug.DebugVars;

public class BTSystem extends EntitySystem {

	private ImmutableArray<Entity> entities;
	
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(BTComponent.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		if(DebugVars.AI_DISABLED) return;
		for(Entity entity : entities) {
			BTComponent btComp = Mappers.bt.get(entity);
			
			if(btComp.tree == null) return;
			btComp.tree.step();
		}
		
		PathFinder.update(deltaTime);
	}
	
}
