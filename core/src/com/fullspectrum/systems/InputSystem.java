package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.InputComponent;

public class InputSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	public InputSystem(){
		super(Family.all(InputComponent.class, FSMComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
	}
	
}
