package com.fullspectrum.fsm.system;

import com.badlogic.ashley.core.Entity;


public class IdlingSystem extends StateSystem{

	private static IdlingSystem instance;
	
	public static IdlingSystem getInstance(){
		if(instance == null) instance = new IdlingSystem();
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		
	}

}
