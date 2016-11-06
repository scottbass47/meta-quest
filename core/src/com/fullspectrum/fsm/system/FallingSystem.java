package com.fullspectrum.fsm.system;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.input.Actions;


public class FallingSystem extends StateSystem{

	private static FallingSystem instance;
	
	public static FallingSystem getInstance(){
		if(instance == null) instance = new FallingSystem();
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(Entity entity : entities){
			SpeedComponent speedComp = Mappers.speed.get(entity);
			InputComponent inputComp = Mappers.input.get(entity);
			
			speedComp.multiplier = Math.abs(inputComp.input.getValue(Actions.MOVE_LEFT) - inputComp.input.getValue(Actions.MOVE_RIGHT));
		}
	}

}
