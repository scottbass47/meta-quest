package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.editor.HelpPanel;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.gui.Window;

public class HelpAction extends Action {

	private Window helpWindow;
	private HelpPanel helpPanel;
	
	public HelpAction() {
		helpPanel = new HelpPanel();
		helpPanel.setPosition(0, 0);
	
		helpWindow = new Window();
		helpWindow.add(helpPanel);
		helpWindow.giveFocus(helpPanel);
		helpWindow.setPosition(GameVars.SCREEN_WIDTH / 2 - helpPanel.getWidth() / 2, GameVars.SCREEN_HEIGHT / 2 - helpPanel.getHeight() / 2);
	}
	
	@Override
	public void onEnter() {
		helpWindow.setHudCamera(hudCamera);
		editor.addProcessor(helpWindow);
	}
	
	@Override
	public void onExit() {
	}
	
	@Override
	public void update(float delta) {
		helpWindow.update(delta);
	}

	@Override
	public void render(SpriteBatch batch) {
		helpWindow.render(batch);
	}
	
	@Override
	public boolean isBlocking() {
		return true;
	}
	
	@Override
	public boolean renderInFront() {
		return true;
	}

}
