package com.fullspectrum.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class IdleState implements IPlayerState{

	@Override
	public void init(Player player) {
		player.setAnimation(PlayerAnim.IDLE);
		player.dx = 0;
		player.dy = 0;
	}
	
	@Override
	public void update(Player player) {
		
	}

	@Override
	public IPlayerState handleInput() {
		if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.D)){
			return new RunningState();
		}
		return null;
	}
}
