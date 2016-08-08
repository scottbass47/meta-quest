package com.fullspectrum.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.fullspectrum.game.GdxGame;

public class IdleState implements IPlayerState {

	private boolean randomIdle = false;
	private boolean wasIdle = false;
	private final static int THRESHOLD = 4;
	private float timePassed = 0.0f;

	@Override
	public void init(Player player) {
		player.setAnimation(PlayerAnim.IDLE);
		player.dx = 0;
		player.dy = 0;
	}

	@Override
	public void update(Player player) {
		timePassed += 1.0f / GdxGame.UPS;
		if (timePassed > THRESHOLD && !wasIdle) {
			if (Math.random() < 1.0f / 250) {
				System.out.println("RANDOM");
				randomIdle = true;
			}
		}
	}

	@Override
	public IPlayerState handleInput() {
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.D)) {
			return new RunningState();
		}
		return null;
	}

	@Override
	public void animFinished(Player player) {
		if (randomIdle) {
			wasIdle = true;
			randomIdle = false;
			player.setAnimation(PlayerAnim.RANDOM_IDLE);
			return;
		}
		if (wasIdle && !randomIdle) {
			player.setAnimation(PlayerAnim.IDLE);
			wasIdle = false;
			timePassed = 0;
			return;
		}
	}
}
