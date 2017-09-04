package com.cpubrew.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.debug.DebugInput;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.input.GameInput;
import com.cpubrew.input.Mouse;

public abstract class AbstractScreen extends ScreenAdapter {

	protected OrthographicCamera worldCamera;
	protected OrthographicCamera hudCamera;
	protected Game game;
	protected SpriteBatch batch;
	protected GameInput input;
	private ArrayMap<ScreenState, Screen> screens;
	private float lag = 0;
	
	public AbstractScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input){
		this.worldCamera = worldCamera;
		this.hudCamera = hudCamera;
		this.game = game;
		this.screens = screens;
		this.input = input;
		batch = new SpriteBatch();
	}
	
	public void setScreen(ScreenState state){
		game.setScreen(screens.get(state));
	}
	
	@Override
	public void render(float delta) {
		lag += delta;
		while(lag > GameVars.UPS_INV){
			update(1.0f / GameVars.UPS / DebugVars.SLOW);
			lag -= 1.0f / GameVars.UPS;
			input.update();
			Mouse.update();
			DebugInput.update();
		}
		render();
	}
	
	@Override
	public void show() {
		super.show();
		init();
	}
	
	@Override
	public void hide(){
		super.hide();
		destroy();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
	}
	
	/** Called once per frame for updating objects */
	public abstract void update(float delta);
	
	/** Called once per frame for rendering graphics */
	public abstract void render();
	
	/** Called when a new Screen gains focus */
	protected abstract void init();
	
	/** Called when Screen loses focus */
	protected abstract void destroy();
	
}