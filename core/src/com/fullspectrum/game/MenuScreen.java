package com.fullspectrum.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.input.GameInput;

public class MenuScreen extends AbstractScreen {

	public MenuScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
	}

	@Override
	protected void init() {
		setScreen(ScreenState.GAME);
	}
	
	@Override
	public void update(float delta) {

	}

	@Override
	public void render() {

	}


	@Override
	protected void destroy() {

	}


}
