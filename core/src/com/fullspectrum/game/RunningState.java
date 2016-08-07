package com.fullspectrum.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class RunningState implements IPlayerState{
	
	@Override
	public void init(Player player) {
		player.setAnimation(PlayerAnim.RUNNING);
	}

	@Override
	public void update(Player player) {
		if(Gdx.input.isKeyPressed(Keys.A) && Gdx.input.isKeyPressed(Keys.D)){
			player.dx = 0;
			System.out.println("both");
		}
		else if(Gdx.input.isKeyPressed(Keys.A)){
			player.dx = -Player.SPEED;
		}
		else if(Gdx.input.isKeyPressed(Keys.D)){
			player.dx = Player.SPEED;
		}
		player.facingRight = player.dx > 0 || player.dx < 0 ? player.dx > 0 : player.facingRight;
	}

	@Override
	public IPlayerState handleInput() {
		if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)){
			return new IdleState();
		}
		return null;
	}

}
