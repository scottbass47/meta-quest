package com.fullspectrum.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ArrayMap;

public class MenuScreen extends AbstractScreen {

	public MenuScreen(OrthographicCamera camera, Game game, ArrayMap<ScreenState, Screen> screens) {
		super(camera, game, screens);
	}

	@Override
	public void update(float delta) {

	}

	@Override
	public void render() {

	}

	@Override
	protected void init() {
		setScreen(ScreenState.GAME);
	}

	@Override
	protected void destroy() {

	}

}
