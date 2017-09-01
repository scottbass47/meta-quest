package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.gui.Window;
import com.fullspectrum.editor.OpenLevelPanel;
import com.fullspectrum.game.GameVars;

public class OpenLevelAction extends Action {

	private Window window;
	private OpenLevelPanel openLevelPanel;
	
	@Override
	public void onEnter() {
		openLevelPanel = new OpenLevelPanel(actionManager);
		openLevelPanel.setPosition(0, 0);
		
		window = editor.getUi().newWindow();
		window.setPosition(GameVars.SCREEN_WIDTH / 2 - openLevelPanel.getWidth() / 2, 450);
		
		window.add(openLevelPanel);
		window.setHudCamera(hudCamera);
	}
	
	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch) {
	}

	@Override
	public void onExit() {
		window.destroy();
	}

	@Override
	public boolean isBlocking() {
		return true;
	}
	
}
