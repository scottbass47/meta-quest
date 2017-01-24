package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.PPM_INV;
import static com.fullspectrum.game.GameVars.SCREEN_HEIGHT;
import static com.fullspectrum.game.GameVars.SCREEN_WIDTH;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fullspectrum.debug.DebugCycle;
import com.fullspectrum.debug.DebugInput;
import com.fullspectrum.debug.DebugKeys;
import com.fullspectrum.debug.DebugToggle;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.input.InputProfile;
import com.fullspectrum.input.RawInput;
/*
 * Global Task List
 * ----------------
 * TODO [AI + WIP] Add wrapper for Level that provides utility functions for simple ai to make decisions
 * TODO [Sound] Add audio system to handle sound effects and music
 * TODO [State Machine] Add in chain transitions
 * TODO [Enemies] Add in enemy spawners
 * TODO [Component] Add entity type to type component (i.e. Player, Slime, Spitter, etc... different from Friendly, Neutral, and Enemy)\
 */
public class GdxGame extends Game {
	// Rendering
	private SpriteBatch batch;
	private OrthographicCamera worldCamera;
	private Viewport worldViewport;
	private OrthographicCamera hudCamera;
	private Viewport hudViewport;
	private BitmapFont font;
	
	// Input
	private RawInput rawInput;
	private GameInput input;
	private InputProfile profile;

	// Screens
	private ArrayMap<ScreenState, Screen> screens;

	// FPS
	public final static int UPS = 60;

	@Override
	public void create() {
		batch = new SpriteBatch();
		worldCamera = new OrthographicCamera();
		worldViewport = new FitViewport(SCREEN_WIDTH * PPM_INV, SCREEN_HEIGHT * PPM_INV, worldCamera);
		worldCamera.zoom = 1.0f;
		hudCamera = new OrthographicCamera();
		hudViewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, hudCamera);
		font = new BitmapFont();
		
		// Setup Game Input
		profile = new InputProfile();
		profile.load("input/input.xml");
		input = new GameInput(profile);
		
		// Setup Raw Input
		rawInput = new RawInput();
		Gdx.input.setInputProcessor(rawInput);
		Controllers.addListener(rawInput);
		rawInput.registerGameInput(input);

		// Initialize Screens
		screens = new ArrayMap<ScreenState, Screen>();
		screens.put(ScreenState.MENU, new MenuScreen(worldCamera, hudCamera, this, screens, input));
		screens.put(ScreenState.GAME, new GameScreen(worldCamera, hudCamera, this, screens, input));
		setScreen(screens.get(ScreenState.MENU));

		// Center HUD Camera
		hudCamera.position.x = SCREEN_WIDTH * 0.5f;
		hudCamera.position.y = SCREEN_HEIGHT * 0.5f;
		
		GLProfiler.enable();
		Gdx.app.setLogLevel(Logger.DEBUG);
	}

	@Override
	public void render() {
		// Clear the Screen
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render();

		hudCamera.update();
		batch.setProjectionMatrix(hudCamera.combined);

		batch.begin();
		if(DebugInput.isToggled(DebugToggle.FPS)){
			font.draw(batch, "" + Gdx.graphics.getFramesPerSecond(), 10, 710);
		}
		if(DebugInput.isToggled(DebugToggle.SHOW_COMMANDS)){
			int startY = DebugToggle.values().length > DebugCycle.values().length ? (DebugToggle.values().length + 1) * 20 : (DebugCycle.values().length + 1) * 20;
			startY += 50;
			int toggleX = 900;
			int cycleX = 1100;
			int keyX = 700;
			font.draw(batch, "Toggles:", toggleX, startY);
			font.draw(batch, "Cycles:", cycleX, startY);
			font.draw(batch, "Keys:", keyX, startY);
			int counter = 1;
			for(DebugToggle toggle : DebugToggle.values()){
				font.draw(batch, toggle.name() + " - '" + toggle.getCharacter() + "'", toggleX, startY - counter * 20);
				counter++;
			}
			counter = 1;
			for(DebugCycle cycle : DebugCycle.values()){
				font.draw(batch, cycle.name() + " - '" + cycle.getCharacter() + "'", cycleX, startY - counter * 20);
				counter++;
			}
			counter = 1;
			for(DebugKeys key : DebugKeys.values()){
				font.draw(batch, key.name() + " - '" + Keys.toString(key.getKey()) + "'", keyX, startY - counter * 20);
				counter++;
			}
		}
		batch.end();
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
