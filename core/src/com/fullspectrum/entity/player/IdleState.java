package com.fullspectrum.entity.player;

import com.fullspectrum.game.GdxGame;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;

public class IdleState extends GroundState implements IPlayerState {

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
		super.update(player);
		timePassed += 1.0f / GdxGame.UPS;
		if (timePassed > THRESHOLD && !wasIdle) {
			if (Math.random() < 1.0f / 250) {
				randomIdle = true;
			}
		}
	}

	@Override
	public IPlayerState handleInput(GameInput input) {
		if (input.getValue(Actions.MOVE_LEFT) > Player.ANALOG_THRESHOLD || input.getValue(Actions.MOVE_RIGHT) > Player.ANALOG_THRESHOLD) {
			return new RunningState();
		}
		return super.handleInput(input);
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