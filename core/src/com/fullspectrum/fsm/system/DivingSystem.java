package com.fullspectrum.fsm.system;

import com.badlogic.ashley.core.Entity;



public class DivingSystem extends StateSystem{

	private static DivingSystem instance;
	
	public static DivingSystem getInstance(){
		if(instance == null) instance = new DivingSystem();
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		
	}

	@Override
	public void onEnter(Entity entity) {
		
	}

	@Override
	public void onExit(Entity entity) {
		
	}
	
}
