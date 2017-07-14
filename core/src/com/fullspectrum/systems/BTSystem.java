package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.BTComponent;
import com.fullspectrum.component.Mappers;

public class BTSystem extends IteratingSystem {

	public BTSystem() {
		super(Family.all(BTComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BTComponent btComp = Mappers.bt.get(entity);
		
		if(btComp.tree == null) return;
		btComp.tree.step();
	}
	
}
