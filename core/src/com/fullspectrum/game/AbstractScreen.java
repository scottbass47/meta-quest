package com.fullspectrum.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.input.GameInput;

public abstract class AbstractScreen extends ScreenAdapter {

	protected OrthographicCamera worldCamera;
	protected OrthographicCamera hudCamera;
	protected Game game;
	protected SpriteBatch batch;
	protected GameInput input;
	private ArrayMap<ScreenState, Screen> screens;
	private float lag = 0;
//	private int ups = 0;
//	private float seconds = 0;
	
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
//		seconds += delta;
		lag += delta;
		while(lag > (1.0f / GdxGame.UPS)){
//			ups++;
			update(1.0f / GdxGame.UPS / 30f);
			lag -= 1.0f / GdxGame.UPS;
			input.update();
		}
//		if(seconds > 1.0f){
//			System.out.println(ups);
//			ups = 0;
//			seconds -= 1.0f;
//		}
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