package com.fullspectrum.fsm.system;



public class AttackSystem extends StateSystem{

	private static AttackSystem instance;
	
	public static AttackSystem getInstance(){
		if(instance == null) instance = new AttackSystem();
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		
	}
	
}
