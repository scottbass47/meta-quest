package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.DirectionComponent.Direction;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;

public class DirectionSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	public DirectionSystem(){
		super(Family.all(InputComponent.class, DirectionComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Input input = Mappers.input.get(entity).input;
		DirectionComponent direction = Mappers.direction.get(entity);
		// IF both left and right are pressed OR neither left and right are pressed, direction is none
		if((input.isPressed(Actions.MOVE_LEFT) && input.isPressed(Actions.MOVE_RIGHT))
			|| !(input.isPressed(Actions.MOVE_LEFT) || input.isPressed(Actions.MOVE_RIGHT))){
			direction.direction = Direction.NONE;
		}
		else if(input.isPressed(Actions.MOVE_LEFT)){
			direction.direction = Direction.LEFT;
		}
		else if(input.isPressed(Actions.MOVE_RIGHT)){
			direction.direction = Direction.RIGHT;
		}
		
	}
	
}
