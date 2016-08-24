package com.fullspectrum.entity.player;

import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.PhysicsComponent;
import com.fullspectrum.entity.Entity;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;

public class PlayerInputComponent extends InputComponent{

	public PlayerInputComponent(GameInput input, PhysicsComponent physics) {
		super(input, physics);
	}

	@Override
	public void init() {
	}

	@Override
	public void update(float delta, Entity entity) {
		if (entity.getEntityStateManager().getCurrentState() instanceof IDirection) {
			if (input.getValue(Actions.MOVE_LEFT) < GameInput.ANALOG_THRESHOLD && input.getValue(Actions.MOVE_RIGHT) < GameInput.ANALOG_THRESHOLD) {
				entity.dx = 0;
			} else if (input.getValue(Actions.MOVE_LEFT) > GameInput.ANALOG_THRESHOLD) {
				entity.dx = -Entity.SPEED * input.getValue(Actions.MOVE_LEFT);
			} else if (input.getValue(Actions.MOVE_RIGHT) > GameInput.ANALOG_THRESHOLD) {
				entity.dx = Entity.SPEED * input.getValue(Actions.MOVE_RIGHT);
			}
		}
	}


}
