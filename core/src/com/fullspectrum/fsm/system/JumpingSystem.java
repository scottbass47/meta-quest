package com.fullspectrum.fsm.system;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.input.Actions;


public class JumpingSystem extends StateSystem{

	private static JumpingSystem instance;
	
	public static JumpingSystem getInstance(){
		if(instance == null) instance = new JumpingSystem();
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(Entity entity : entities){
			JumpComponent jumpComp = Mappers.jump.get(entity);
			InputComponent inputComp = Mappers.input.get(entity);
			
			if(jumpComp != null) jumpComp.multiplier = inputComp.input.getValue(Actions.JUMP);
		}
	}
	
}
