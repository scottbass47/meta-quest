package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.NewLevelPanel;
import com.cpubrew.game.GameVars;
import com.cpubrew.gui.Window;

public class NewLevelAction extends Action {

	private Window window;
	private NewLevelPanel newLevelPanel;
	
	@Override
	public void onEnter() {
		newLevelPanel = new NewLevelPanel(actionManager);
		newLevelPanel.setPosition(0, 0);
		
		window = editor.getUi().newWindow();
		window.setPosition(GameVars.SCREEN_WIDTH / 2 - newLevelPanel.getWidth() / 2, 450);
		
		window.add(newLevelPanel);
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
