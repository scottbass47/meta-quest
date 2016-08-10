package com.fullspectrum.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.entity.player.Player;
import com.fullspectrum.input.GameInput;

public class GameScreen extends AbstractScreen {

	// Debug Graphics
	private ShapeRenderer sRenderer;
	
	// Player
	private Player player;

	public GameScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
		sRenderer = new ShapeRenderer();
		player = new Player();
	}

	@Override
	protected void init() {
		worldCamera.position.x = GdxGame.WORLD_WIDTH * 0.5f;
		worldCamera.position.y = GdxGame.WORLD_HEIGHT * 0.5f;
	}

	@Override
	public void handleInput(){
		player.handleInput(input);
	}
	
	@Override
	public void update(float delta) {
		player.update(delta);
		worldCamera.position.x += 20.0f * delta;
	}

	@Override
	public void render() {
		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);
		sRenderer.setProjectionMatrix(worldCamera.combined);

		batch.begin();
		player.render(batch);
		batch.end();
	}
	

	@Override
	protected void destroy() {
		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		sRenderer.dispose();
		player.dispose();
	}
}
