package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.HelpPanel;
import com.cpubrew.game.GameVars;
import com.cpubrew.gui.Window;

public class HelpAction extends Action {

	private Window helpWindow;
	private HelpPanel helpPanel;
	
	public HelpAction() {
		helpPanel = new HelpPanel();
		helpPanel.setPosition(0, 0);
	}
	
	@Override
	public void onEnter() {
		helpWindow = editor.getUi().newWindow();
		helpWindow.add(helpPanel);
		helpWindow.setPosition(GameVars.SCREEN_WIDTH / 2 - helpPanel.getWidth() / 2, GameVars.SCREEN_HEIGHT / 2 - helpPanel.getHeight() / 2);
	}
	
	@Override
	public void onExit() {
		helpWindow.destroy();
	}
	
	@Override
	public boolean isBlocking() {
		return true;
	}
	
	@Override
	public boolean renderInFront() {
		return true;
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch) {
	}

}
