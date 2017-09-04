package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.NewLevelPanel;
import com.cpubrew.game.GameVars;

public class NewLevelAction extends EditorAction {

//	private Window window;
	private NewLevelPanel newLevelPanel;
	
	@Override
	public void onEnter() {
		newLevelPanel = new NewLevelPanel(actionManager);
		newLevelPanel.setPosition(GameVars.SCREEN_WIDTH / 2 - newLevelPanel.getWidth() / 2, 450);
		
		editor.getEditorWindow().add(newLevelPanel);
		
//		window = new Window("New Level");
//		window.setPosition(GameVars.SCREEN_WIDTH / 2 - newLevelPanel.getWidth() / 2, 450);
//		
//		window.add(newLevelPanel);
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
		editor.getEditorWindow().remove(newLevelPanel);
//		window.close();
	}

	@Override
	public boolean isBlocking() {
		return true;
	}
	
}
