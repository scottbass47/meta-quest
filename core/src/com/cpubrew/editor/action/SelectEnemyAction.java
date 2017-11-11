package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.EnemyPanel;
import com.cpubrew.editor.SelectListener;
import com.cpubrew.editor.mapobject.MapObjectFactory;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.game.GameVars;

public class SelectEnemyAction extends EditorAction {

//	private Window enemyWindow;
	private EnemyPanel enemyPanel;
	private EntityIndex selectedEntity;
	
	public SelectEnemyAction() {
		enemyPanel = new EnemyPanel();
		enemyPanel.setPosition(GameVars.SCREEN_WIDTH / 2 - enemyPanel.getWidth() / 2, GameVars.SCREEN_HEIGHT / 2 - enemyPanel.getHeight() / 2);

		enemyPanel.addListener(new SelectListener() {
			@Override
			public void onSelect(EntityIndex index) {
				selectedEntity = index;
				actionManager.switchAction(EditorActions.PLACE);
				PlaceAction placeAction = (PlaceAction) actionManager.getCurrentActionInstance();
				placeAction.setPlaceable(MapObjectFactory.createSpawnpoint(editor, index));
			}
		});
	}
	
	@Override
	public void onEnter() {
		editor.getEditorWindow().add(enemyPanel);
//		enemyWindow = new Window("Select Enemey");
//		enemyWindow.add(enemyPanel);
//		enemyWindow.setPosition(GameVars.SCREEN_WIDTH / 2 - enemyPanel.getWidth() / 2, GameVars.SCREEN_HEIGHT / 2 - enemyPanel.getHeight() / 2);
//		enemyWindow.setVisible(true);
//		enemyWindow.setRenderBackground(false);
	}
	
	@Override
	public void onExit() {
		editor.getEditorWindow().remove(enemyPanel);
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
