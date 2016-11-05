package com.fullspectrum.fsm.system;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.SwordComponent;
import com.fullspectrum.component.SwordStatsComponent;




public class AttackSystem extends StateSystem{

	private static AttackSystem instance;
	
	public static AttackSystem getInstance(){
		if(instance == null) instance = new AttackSystem();
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		
	}

	@Override
	public void onEnter(Entity entity) {
		SwingComponent swingComp = Mappers.swing.get(entity);
		swingComp.time = 0;
		
		SwordComponent swordComp = Mappers.sword.get(entity);
		SwordStatsComponent swordStats = Mappers.swordStats.get(swordComp.sword);
		swordStats.hitEntities.clear();
		
		EngineComponent engineComp = Mappers.engine.get(entity);
		engineComp.engine.addEntity(swordComp.sword);
		
		Mappers.body.get(swordComp.sword).body.setActive(true);
	}

	@Override
	public void onExit(Entity entity) {
		SwordComponent swordComp = Mappers.sword.get(entity);
		
		EngineComponent engineComp = Mappers.engine.get(entity);
		engineComp.engine.removeEntity(swordComp.sword);
		
		Mappers.body.get(swordComp.sword).body.setActive(false);
	}
	
}
