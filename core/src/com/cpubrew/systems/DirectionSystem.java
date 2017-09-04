package com.cpubrew.systems;

import static com.cpubrew.input.GameInput.ANALOG_THRESHOLD;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.cpubrew.component.DirectionComponent;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.DirectionComponent.Direction;
import com.cpubrew.input.Actions;
import com.cpubrew.input.GameInput;
import com.cpubrew.input.Input;

public class DirectionSystem extends IteratingSystem {

	public DirectionSystem() {
		super(Family.all(InputComponent.class, DirectionComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Input input = Mappers.input.get(entity).input;
		DirectionComponent direction = Mappers.direction.get(entity);

		if (input instanceof GameInput) {
			if ((input.getValue(Actions.MOVE_LEFT) > ANALOG_THRESHOLD && input.getValue(Actions.MOVE_RIGHT) > ANALOG_THRESHOLD) || !(input.getValue(Actions.MOVE_LEFT) > ANALOG_THRESHOLD || input.getValue(Actions.MOVE_RIGHT) > ANALOG_THRESHOLD)) {
				direction.direction = Direction.NONE;
			}
			else if (input.getValue(Actions.MOVE_LEFT) > ANALOG_THRESHOLD) {
				direction.direction = Direction.LEFT;
			}
			else if (input.getValue(Actions.MOVE_RIGHT) > ANALOG_THRESHOLD) {
				direction.direction = Direction.RIGHT;
			}
			return;
		}

		// IF both left and right are pressed OR neither left and right are
		// pressed, direction is none
		if ((input.isPressed(Actions.MOVE_LEFT) && input.isPressed(Actions.MOVE_RIGHT)) || !(input.isPressed(Actions.MOVE_LEFT) || input.isPressed(Actions.MOVE_RIGHT))) {
			direction.direction = Direction.NONE;
		}
		else if (input.isPressed(Actions.MOVE_LEFT)) {
			direction.direction = Direction.LEFT;
		}
		else if (input.isPressed(Actions.MOVE_RIGHT)) {
			direction.direction = Direction.RIGHT;
		}

	}

}
