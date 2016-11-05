package com.fullspectrum.fsm.system;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.input.Actions;

public class RunningSystem extends StateSystem{

	private static RunningSystem instance;
	
	public static RunningSystem getInstance(){
		if(instance == null) instance = new RunningSystem();
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

	@Override
	public void onEnter(Entity entity) {
		
	}

	@Override
	public void onExit(Entity entity) {
		
	}
	
}
