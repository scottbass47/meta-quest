package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.editor.EnemyPanel;
import com.fullspectrum.editor.SelectListener;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.game.GameVars;

public class SelectEnemyAction extends Action {

	private EnemyPanel enemyPanel;
	private EntityIndex selectedEntity;

	public SelectEnemyAction() {
		enemyPanel = new EnemyPanel();
		enemyPanel.setPosition(GameVars.SCREEN_WIDTH * 0.5f - enemyPanel.getWidth() * 0.5f, GameVars.SCREEN_HEIGHT * 0.5f - enemyPanel.getHeight() * 0.5f);
	
		enemyPanel.addListener(new SelectListener() {
			@Override
			public void onSelect(EntityIndex index) {
				selectedEntity = index;
				enemyPanel.hide();
				actionManager.switchAction(EditorActions.PLACE_SPAWNPOINT);
			}
		});
		
		enemyPanel.show();
	}
	
	@Override
	public void init() {
		enemyPanel.setHudCamera(hudCamera);
	}
	
	public EntityIndex getSelectedEntity() {
		return selectedEntity;
	}
	
	@Override
	public void update(float delta) {
		enemyPanel.update(delta);
	}

	@Override
	public void render(SpriteBatch batch) {
		enemyPanel.render(batch);
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
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector2 hudCoords = editor.toHudCoords(screenX, screenY);
		enemyPanel.touchUp((int)hudCoords.x, (int)hudCoords.y, pointer, button);
		return false;
	}
}
