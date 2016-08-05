package com.fullspectrum.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ArrayMap;

public abstract class AbstractScreen extends ScreenAdapter{

	protected OrthographicCamera camera;
	protected Game game;
	protected SpriteBatch batch;
	private ArrayMap<ScreenState, Screen> screens;
	
	public AbstractScreen(OrthographicCamera camera, Game game, ArrayMap<ScreenState, Screen> screens){
		this.camera = camera;
		this.game = game;
		batch = new SpriteBatch();
		this.screens = screens;
	}
	
	public void setScreen(ScreenState state){
		game.setScreen(screens.get(state));
	}
	
	@Override
	public void render(float delta) {
		update(delta);
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
	
	/** Called once per frame for updating objects */
	public abstract void update(float delta);
	
	/** Called once per frame for rendering graphics */
	public abstract void render();
	
	/** Called when a new Screen gains focus */
	protected abstract void init();
	
	/** Called when Screen loses focus */
	protected abstract void destroy();
	
}