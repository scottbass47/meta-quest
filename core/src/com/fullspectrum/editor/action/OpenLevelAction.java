package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.editor.OpenLevelPanel;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.gui.Window;

public class OpenLevelAction extends Action {

	private Window window;
	private OpenLevelPanel openLevelPanel;
	
	@Override
	public void onEnter() {
		openLevelPanel = new OpenLevelPanel(actionManager);
		openLevelPanel.setPosition(0, 0);
		
		window = new Window();
		window.setPosition(GameVars.SCREEN_WIDTH / 2 - openLevelPanel.getWidth() / 2, 450);
		
		window.add(openLevelPanel);
		window.giveFocus(openLevelPanel);
		window.setHudCamera(hudCamera);

		editor.addProcessor(window);
	}
	
	@Override
	public void update(float delta) {
		window.update(delta);
	}

	@Override
	public void render(SpriteBatch batch) {
		window.render(batch);
	}

	@Override
	public void onExit() {
		editor.removeInputProcessor(window);
	}

	@Override
	public boolean isBlocking() {
		return true;
	}
	
}
