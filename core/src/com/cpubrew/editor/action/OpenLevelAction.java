package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.OpenLevelPanel;
import com.cpubrew.game.GameVars;

public class OpenLevelAction extends EditorAction {

//	private Window window;
	private OpenLevelPanel openLevelPanel;
	
	@Override
	public void onEnter() {
		openLevelPanel = new OpenLevelPanel(actionManager);
		openLevelPanel.setPosition(GameVars.SCREEN_WIDTH / 2 - openLevelPanel.getWidth() / 2, 450);
		
		editor.getEditorWindow().add(openLevelPanel);
		
//		window = new Window("Open Level");
//		window.setPosition(GameVars.SCREEN_WIDTH / 2 - openLevelPanel.getWidth() / 2, 450);
//		
//		window.add(openLevelPanel);
//		window.setVisible(true);
	}
	
	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch) {
	}

	@Override
	public void onExit() {
		editor.getEditorWindow().remove(openLevelPanel);
//		window.close();
	}

	@Override
	public boolean isBlocking() {
		return true;
	}
	
}
