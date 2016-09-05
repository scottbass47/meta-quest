package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.R_WORLD_HEIGHT;
import static com.fullspectrum.game.GameVars.R_WORLD_WIDTH;
import static com.fullspectrum.game.GameVars.V_WORLD_HEIGHT;
import static com.fullspectrum.game.GameVars.V_WORLD_WIDTH;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.input.InputProfile;

public class GdxGame extends Game {
	// Rendering
	private SpriteBatch batch;
	private OrthographicCamera worldCamera;
	private Viewport worldViewport;
	private OrthographicCamera hudCamera;
	private Viewport hudViewport;
	private BitmapFont font;
	
	// Input
	private GameInput input;
	private InputProfile profile;

	// Screens
	private ArrayMap<ScreenState, Screen> screens;

	// FPS Logging
	public final static int UPS = 60;
//	private int fps = 0;
//	private int drawFPS = fps;
//	private long startTime = System.nanoTime();
//	private boolean fpsOn = false;
//	private FPSLogger fpsLogger;
//	private boolean prevPressed = false;

	@Override
	public void create() {
		batch = new SpriteBatch();
		worldCamera = new OrthographicCamera();
		worldViewport = new FitViewport(R_WORLD_WIDTH , R_WORLD_HEIGHT, worldCamera);
		hudCamera = new OrthographicCamera();
		hudViewport = new FitViewport(V_WORLD_WIDTH, V_WORLD_HEIGHT, hudCamera);
		font = new BitmapFont();
//		fpsLogger = new FPSLogger();
		
		// Setup Input
		profile = new InputProfile();
		profile.load("input/input.xml");
		input = new GameInput(profile);

		// Initialize Screens
		screens = new ArrayMap<ScreenState, Screen>();
		screens.put(ScreenState.MENU, new MenuScreen(worldCamera, hudCamera, this, screens, input));
		screens.put(ScreenState.GAME, new GameScreen(worldCamera, hudCamera, this, screens, input));
		setScreen(screens.get(ScreenState.MENU));

		// Center HUD Camera
		hudCamera.position.x = V_WORLD_WIDTH * 0.5f;
		hudCamera.position.y = V_WORLD_HEIGHT * 0.5f;
		
		GLProfiler.enable();
	}

	@Override
	public void render() {
		// Clear the Screen
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();

		hudCamera.update();
		batch.setProjectionMatrix(hudCamera.combined);

//		batch.begin();
//		fps++;
//		if (fpsOn) {
//			font.draw(batch, "" + drawFPS, 10, 710);
//		}
//		batch.end();
//
//		// Setup P to Toggle FPS
//		if (Gdx.input.isKeyPressed(Keys.P)) {
//			if (!prevPressed)
//				fpsOn = !fpsOn;
//			prevPressed = false;
//			if (fpsOn) {
//				startTime = System.nanoTime();
//				fps = 0;
//			}
//		}
//		prevPressed = Gdx.input.isKeyPressed(Keys.P);
//
//		// FPS and Controller
//		if ((System.nanoTime() - startTime) / 1000000 > 1000) {
//			if (fpsOn) {
//				drawFPS = fps;
//				fps = 0;
//				fpsLogger.log();
//			}
////			System.out.printf("Calls: %d, Draw Calls: %d\n", GLProfiler.calls, GLProfiler.drawCalls);
//			startTime = System.nanoTime();
////			input.update();
//		}
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		font.dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		worldViewport.update(width, height);
		hudViewport.update(width, height);
	}
}
