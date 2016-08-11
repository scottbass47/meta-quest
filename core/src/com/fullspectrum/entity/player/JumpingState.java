package com.fullspectrum.entity.player;

import com.fullspectrum.input.GameInput;

public class JumpingState implements IPlayerState, IDirection{

	private boolean rising = true;
	
	@Override
	public void init(Player player) {
		player.setAnimation(PlayerAnim.JUMP);
		player.jump();
	}

	@Override
	public void update(Player player) {
		if(player.jumping){
			// Falling now
			if(player.dy < 0.0f){
				player.setPlayerState(new FallingState());
			}
		}
	}

	@Override
	public IPlayerState handleInput(GameInput input) {
		return null;
	}

	@Override
	public void animFinished(Player player) {
		if(rising){
			rising = false;
			player.setAnimation(PlayerAnim.RISE);
		}
	}

}
