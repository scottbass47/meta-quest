package com.fullspectrum.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends AbstractScreen {

	// HUD
	private Viewport viewportHUD;
	private OrthographicCamera cameraHUD;

	// Debug Graphics
	private ShapeRenderer sRenderer;
	
	// Player
	private Player player;

	public GameScreen(OrthographicCamera camera, Game game, ArrayMap<ScreenState, Screen> screens) {
		super(camera, game, screens);
		cameraHUD = new OrthographicCamera();
		viewportHUD = new FitViewport(GdxGame.WORLD_WIDTH, GdxGame.WORLD_HEIGHT, cameraHUD);
		sRenderer = new ShapeRenderer();
		player = new Player();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewportHUD.update(width, height);
	}

	@Override
	protected void init() {
		camera.position.x = GdxGame.WORLD_WIDTH * 0.5f;
		camera.position.y = GdxGame.WORLD_HEIGHT * 0.5f;
	}

	@Override
	public void handleInput(){
		player.handleInput();
	}
	
	@Override
	public void update(float delta) {
		player.update(delta);
	}

	@Override
	public void render() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		sRenderer.setProjectionMatrix(camera.combined);

		batch.begin();
		player.render(batch);
		batch.end();
	}
	

	@Override
	protected void destroy() {

	}

}
