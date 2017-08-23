package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.editor.NewLevelPanel;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.gui.Window;

public class NewLevelAction extends Action {

	private Window window;
	private NewLevelPanel newLevelPanel;
	
	@Override
	public void onEnter() {
		newLevelPanel = new NewLevelPanel(actionManager);
		newLevelPanel.setPosition(0, 0);
		
		window = new Window();
		window.setPosition(GameVars.SCREEN_WIDTH / 2 - newLevelPanel.getWidth() / 2, 450);
		
		window.add(newLevelPanel);
		window.giveFocus(newLevelPanel);
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
		editor.removeProcessor(window);
	}

	@Override
	public boolean isBlocking() {
		return true;
	}
	
}
