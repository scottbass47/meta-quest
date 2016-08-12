package com.fullspectrum.entity.player;

import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;

public class GroundState implements IPlayerState{

	@Override
	public void init(Player player) {
	}

	@Override
	public void update(Player player) {
	}

	@Override
	public IPlayerState handleInput(GameInput input) {
		if(input.isJustPressed(Actions.JUMP)){
			return new JumpingState();
		}
		return null;
	}

	@Override
	public void animFinished(Player player) {
	}

}
