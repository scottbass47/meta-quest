package com.fullspectrum.fsm.system;


public class FallingSystem extends StateSystem{

	private static FallingSystem instance;
	
	public static FallingSystem getInstance(){
		if(instance == null) instance = new FallingSystem();
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		
	}
	
}
