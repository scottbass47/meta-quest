package com.fullspectrum.arena;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.game.GdxGame;
import com.fullspectrum.level.LevelManager;
import com.fullspectrum.utils.EntityUtils;

public class Arena {
	
	private LevelManager levelManager;
	private ArenaState state;
	private PickPlayerScreen playerScreen;
	private ArenaGame game;
	private ArenaDeathScreen deathScreen;
	private boolean stopped = true;
	
	public Arena(LevelManager levelManager, OrthographicCamera hudCamera) {
		this.levelManager = levelManager;
		
		game = new ArenaGame(this, hudCamera);
		playerScreen = new PickPlayerScreen(this, hudCamera);
		deathScreen = new ArenaDeathScreen(this, hudCamera);
	}
	
	public void start() {
		stopped = false;
		switchState(ArenaState.PICKING_PLAYER, true);
	}
	
	public void stop() {
		stopped = true;
		GdxGame.input.removeInput(playerScreen);
		GdxGame.input.removeInput(deathScreen);
		game.reset();
	}
	
	protected void switchState(ArenaState state) {
		switchState(state, false);
	}
	
	protected void switchState(ArenaState state, boolean onStart) {
		ArenaState previousState = this.state;
		
		// Cleanup old state
		if(previousState != null && !onStart) {
			switch(previousState) {
			case PICKING_PLAYER:
				GdxGame.input.removeInput(playerScreen);
				if(!EntityUtils.isValid(EntityUtils.getPlayer())) {
					levelManager.spawnPlayer(levelManager.getCurrentLevel());
				}
				levelManager.switchPlayer(playerScreen.getSelectedPlayer());
				break;
			case PLAYING:
				break;
			case DEATH_SCREEN:
				GdxGame.input.removeInput(deathScreen);
				break;
			default:
				break;
			}
		}
		
		// Set new state
		this.state = state;
		
		// Init new state
		switch(state) {
		case PICKING_PLAYER:
			GdxGame.input.addFirst(playerScreen);
			break;
		case PLAYING:
			game.start();
			break;
		case DEATH_SCREEN:
			GdxGame.input.addFirst(deathScreen);
			break;
		default:
			break;
		}
	}
	
	public void update(float delta) {
		if(stopped) return;
		
		switch(state) {
		case PICKING_PLAYER:
			playerScreen.update(delta);
			break;
		case PLAYING:
			game.update(delta);
			break;
		case DEATH_SCREEN:
			deathScreen.update(delta);
			break;
		default:
			break;
		}
	}
	
	public void renderHUD(SpriteBatch batch) {
		if(stopped) return;
		
		switch(state) {
		case PICKING_PLAYER:
			playerScreen.render(batch);
			break;
		case PLAYING:
			game.renderHUD(batch);
			break;
		case DEATH_SCREEN:
			deathScreen.render(batch);
			break;
		default:
			break;
		}
	}
	
	public boolean isFinished() {
		return game.isFinished();
	}

	public boolean isActive() {
		return !stopped;
	}
	
	public void load(FileHandle config) {
		game.load(config);
	}
	
	@Override
	public String toString() {
		return game.toString();
	}
	
}
