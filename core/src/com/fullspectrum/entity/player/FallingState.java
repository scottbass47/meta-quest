package com.fullspectrum.entity.player;

import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;

public class FallingState implements IPlayerState, IDirection {

	private boolean doneJumping = false;

	@Override
	public void init(Player player) {
		player.setAnimation(PlayerAnim.RISE);
	}

	@Override
	public void update(Player player) {
		doneJumping = !player.jumping;
	}

	@Override
	public IPlayerState handleInput(GameInput input) {
		if (doneJumping) {
			if (input.getValue(Actions.MOVE_LEFT) < Player.ANALOG_THRESHOLD && input.getValue(Actions.MOVE_RIGHT) < Player.ANALOG_THRESHOLD) {
				return new IdleState();
			} else {
				return new RunningState();
			}
		}
		return null;
	}

	@Override
	public void animFinished(Player player) {
	}

}
