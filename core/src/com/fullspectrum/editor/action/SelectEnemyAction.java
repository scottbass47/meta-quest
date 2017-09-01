package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.gui.Window;
import com.fullspectrum.editor.EnemyPanel;
import com.fullspectrum.editor.PlaceableSpawnpoint;
import com.fullspectrum.editor.SelectListener;
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
				selectedEntity = index;
				actionManager.switchAction(EditorActions.PLACE);
				PlaceAction placeAction = (PlaceAction) actionManager.getCurrentActionInstance();
				placeAction.setPlaceable(new PlaceableSpawnpoint(index));
			}
		});
	}
	
	@Override
	public void onEnter() {
		enemyWindow = editor.getUi().newWindow();
		enemyWindow.add(enemyPanel);
		enemyWindow.setPosition(GameVars.SCREEN_WIDTH / 2 - enemyPanel.getWidth() / 2, GameVars.SCREEN_HEIGHT / 2 - enemyPanel.getHeight() / 2);
	}
	
	@Override
	public void onExit() {
		enemyWindow.destroy();
	}
	
	public EntityIndex getSelectedEntity() {
		return selectedEntity;
	}
	
	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch) {
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
