package com.cpubrew.arena;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.game.GdxGame;
import com.cpubrew.gui.UIManager;
import com.cpubrew.gui.Window;
import com.cpubrew.level.LevelManager;
import com.cpubrew.utils.EntityUtils;

public class Arena {
	
	private LevelManager levelManager;
	private Window arenaWindow;
	private ArenaState state;
	private PickPlayerScreen playerScreen;
	private ArenaGame game;
	private ArenaDeathScreen deathScreen;
	private boolean stopped = true;
	
	public Arena(LevelManager levelManager, OrthographicCamera hudCamera, UIManager ui) {
		this.levelManager = levelManager;
		
		arenaWindow = ui.newWindow("Arena");
		
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
				if(!EntityUtils.isValid(EntityUtils.getPlayer())) {
					levelManager.spawnPlayer(levelManager.getCurrentLevel());
				}
				levelManager.switchPlayer(playerScreen.getSelectedPlayer());
				break;
			default:
				break;
			}
		}
		
		// Set new state
		this.state = state;
		
		// Init new state
		arenaWindow.removeAll();

		switch(state) {
		case PICKING_PLAYER:
			arenaWindow.add(playerScreen);
			break;
		case PLAYING:
			game.start();
			break;
		case DEATH_SCREEN:
			arenaWindow.add(deathScreen);
			break;
		default:
			break;
		}
	}
	
	public void update(float delta) {
		if(stopped) return;
		
		switch(state) {
		case PLAYING:
			game.update(delta);
			break;
		default:
			break;
		}
	}
	
	public void renderHUD(SpriteBatch batch) {
		if(stopped) return;
		
		switch(state) {
		case PLAYING:
			game.renderHUD(batch);
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
