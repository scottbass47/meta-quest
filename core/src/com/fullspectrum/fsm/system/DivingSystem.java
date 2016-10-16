package com.fullspectrum.fsm.system;


public class DivingSystem extends StateSystem{

	private static DivingSystem instance;
	
	public static DivingSystem getInstance(){
		if(instance == null) instance = new DivingSystem();
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		
	}
	
}
