package com.fullspectrum.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class RunningState implements IPlayerState, IDirection{
	
	@Override
	public void init(Player player) {
		player.setAnimation(PlayerAnim.RUNNING);
	}

	@Override
	public void update(Player player) {
		
	}

	@Override
	public IPlayerState handleInput() {
		if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)){
			return new IdleState();
		}
		return null;
	}

	@Override
	public void animFinished(Player player) {
		
	}

}
