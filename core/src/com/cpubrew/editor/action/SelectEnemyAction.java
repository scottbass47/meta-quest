package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.EnemyPanel;
import com.cpubrew.editor.PlaceableSpawnpoint;
import com.cpubrew.editor.SelectListener;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.game.GameVars;
import com.cpubrew.gui.Window;

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
