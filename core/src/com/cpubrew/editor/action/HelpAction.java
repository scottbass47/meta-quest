package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.HelpPanel;

public class HelpAction extends EditorAction {

//	private Window helpWindow;
	private HelpPanel helpPanel;
	
	@Override
	public void onEnter() {
		helpPanel = new HelpPanel();
		helpPanel.setPosition(0, 0);
		
		editor.getEditorWindow().add(helpPanel);
		
//		helpWindow = new Window();
//		helpWindow.add(helpPanel);
//		helpWindow.setPosition(GameVars.SCREEN_WIDTH / 2 - helpPanel.getWidth() / 2, GameVars.SCREEN_HEIGHT / 2 - helpPanel.getHeight() / 2);
//		helpWindow.setVisible(true);
	}
	
	@Override
	public void onExit() {
		editor.getEditorWindow().remove(helpPanel);
//		helpWindow.close();
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
