package com.fullspectrum.entity.player;

import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;

public class RunningState implements IPlayerState, IDirection{
	
	@Override
	public void init(Player player) {
		player.setAnimation(PlayerAnim.RUNNING);
	}

	@Override
	public void update(Player player) {
		
	}

	@Override
	public IPlayerState handleInput(GameInput input) {
		if(input.getValue(Actions.MOVE_LEFT) < Player.ANALOG_THRESHOLD && input.getValue(Actions.MOVE_RIGHT) < Player.ANALOG_THRESHOLD){
			return new IdleState();
		}
		return null;
	}

	@Override
	public void animFinished(Player player) {
		
	}
}
