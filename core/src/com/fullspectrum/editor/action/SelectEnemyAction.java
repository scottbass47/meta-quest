package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.fullspectrum.editor.EnemyPanel;
import com.fullspectrum.editor.PlaceableSpawnpoint;
import com.fullspectrum.editor.SelectListener;
import com.fullspectrum.editor.gui.Window;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.game.GameVars;

public class SelectEnemyAction extends Action {

	private Window enemyWindow;
	private EnemyPanel enemyPanel;
	private EntityIndex selectedEntity;
	
	public SelectEnemyAction() {
		enemyPanel = new EnemyPanel();
		enemyPanel.setPosition(0, 0);
	
		enemyPanel.addListener(new SelectListener() {
			@Override
			public void onSelect(EntityIndex index) {
				editor.removeProcessor(enemyWindow);
				selectedEntity = index;
//				enemyPanel.hide();
				actionManager.switchAction(EditorActions.PLACE);
				PlaceAction placeAction = (PlaceAction) actionManager.getCurrentActionInstance();
				placeAction.setPlaceable(new PlaceableSpawnpoint(index));
			}
		});
		
//		enemyPanel.show();
		
		enemyWindow = new Window();
		enemyWindow.add(enemyPanel);
		enemyWindow.giveFocus(enemyPanel);
		enemyWindow.setPosition(GameVars.SCREEN_WIDTH / 2 - enemyPanel.getWidth() / 2, GameVars.SCREEN_HEIGHT / 2 - enemyPanel.getHeight() / 2);
	}
	
	@Override
	public void init() {
		enemyWindow.setHudCamera(hudCamera);
		editor.addProcessor(enemyWindow);
	}
	
	public EntityIndex getSelectedEntity() {
		return selectedEntity;
	}
	
	@Override
	public void update(float delta) {
		enemyWindow.update(delta);
	}

	@Override
	public void render(SpriteBatch batch) {
		enemyWindow.render(batch);
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
