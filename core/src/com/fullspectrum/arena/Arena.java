package com.fullspectrum.arena;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.game.GdxGame;
import com.fullspectrum.level.LevelManager;
import com.fullspectrum.utils.EntityUtils;

public class Arena {
	
	// Pick character screen
	// Arena renderer
	// Death screen
	private LevelManager levelManager;
	private ArenaState state;
	private PickPlayerScreen playerScreen;
	private ArenaGame game;
	private ArenaDeathScreen deathScreen;
	
	public Arena(LevelManager levelManager, OrthographicCamera hudCamera) {
		this.levelManager = levelManager;
		
		game = new ArenaGame(this, hudCamera);
		playerScreen = new PickPlayerScreen(this, hudCamera);
		deathScreen = new ArenaDeathScreen(this, hudCamera);
	}
	
	public void start() {
		switchState(ArenaState.DEATH_SCREEN);
	}
	
	public void cleanUp() {
		game.reset();
	}
	
	protected void switchState(ArenaState state) {
		ArenaState previousState = this.state;
		
		// Cleanup old state
		if(previousState != null) {
			switch(previousState) {
			case PICKING_PLAYER:
				GdxGame.input.removeInput(playerScreen);
				
				if(!EntityUtils.isValid(EntityUtils.getPlayer())) {
					levelManager.spawnPlayer(levelManager.getCurrentLevel());
				} else {
					levelManager.switchPlayer(playerScreen.getSelectedPlayer());
				}
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
	
	public void load(FileHandle config) {
		game.load(config);
	}
	
	@Override
	public String toString() {
		return game.toString();
	}
	
}
