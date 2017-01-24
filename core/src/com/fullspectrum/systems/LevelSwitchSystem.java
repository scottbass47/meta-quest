package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.LevelSwitchComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PlayerComponent;
import com.fullspectrum.input.Actions;

public class LevelSwitchSystem extends IteratingSystem{

	public LevelSwitchSystem() {
		super(Family.all(LevelSwitchComponent.class, PlayerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputComponent inputComp = Mappers.input.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);
		LevelSwitchComponent switchComp = Mappers.levelSwitch.get(entity);
		
		if(inputComp.input.isPressed(Actions.MOVE_UP)){
			// switch
			System.out.println("Switching...");
			entity.remove(LevelSwitchComponent.class);
		}
	}
}
