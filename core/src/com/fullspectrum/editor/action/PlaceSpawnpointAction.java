package com.fullspectrum.editor.action;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.ExpandableGrid;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.utils.Maths;

public class PlaceSpawnpointAction extends Action {

	private EntityIndex entityIndex;
	private boolean facingRight = true;
	private float animTime = 0.0f;
	
	@Override
	public void init() {
		SelectEnemyAction selectEnemyAction  = (SelectEnemyAction) actionManager.getPreviousActionInstance();
		entityIndex = selectEnemyAction.getSelectedEntity();
	}
	
	@Override
	public void update(float delta) {
		animTime += delta;
	}

	@Override
	public void render(SpriteBatch batch) {
		Vector2 worldCoords = editor.toWorldCoords(editor.getMousePos());
		
		float x = worldCoords.x;
		float y = worldCoords.y;
		
		int row = Maths.toGridCoord(y);
		
		Animation idle = entityIndex.getIdleAnimation();
		Rectangle rect = entityIndex.getHitBox();
		TextureRegion region = idle.getKeyFrame(animTime);
		float w = region.getRegionWidth();
		float h = region.getRegionHeight();
		
		float adjustedY = row + GameVars.PPM_INV * (rect.height * 0.5f);
		float yy =  adjustedY - h * 0.5f;
		
		float hitX = x - GameVars.PPM_INV * (rect.width * 0.5f);
		float hitY = yy + h * 0.5f - GameVars.PPM_INV * (rect.height * 0.5f);
		
		if(collidingWithMap(hitX, hitY, GameVars.PPM_INV * rect.width, GameVars.PPM_INV * rect.height)){
			batch.setColor(Color.RED);
		} 
		
		if(!facingRight) {
			region.flip(true, false);
		}
		
		batch.setProjectionMatrix(worldCamera.combined);
		batch.begin();
		batch.draw(region, x - w * 0.5f, yy, w * 0.5f, h * 0.5f, w, h, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f);
		batch.end();

		region.flip(region.isFlipX(), false);
		batch.setColor(Color.WHITE);
	}

	private boolean collidingWithMap(float x, float y, float width, float height) {
		int minRow = Math.abs(y - (int) y) < 0.0005f ? (int)y : Maths.toGridCoord(y);
		int minCol = Maths.toGridCoord(x);
		int maxRow = Maths.toGridCoord(y + height);
		int maxCol = Maths.toGridCoord(x + width);
		
		ExpandableGrid<MapTile> tileMap = editor.getTileMap();
		
		for(int row = minRow; row <= maxRow; row++) {
			for(int col = minCol; col <= maxCol; col++) {
				if(tileMap.contains(row, col) && tileMap.get(row, col) != null && tileMap.get(row, col).isSolid()) return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.R) {
			facingRight = !facingRight;
		}
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Rectangle rect = entityIndex.getHitBox();

		Vector2 worldCoords = editor.toWorldCoords(editor.toHudCoords(screenX, screenY));
		
		int row = Maths.toGridCoord(worldCoords.y);
		
		float hitX = worldCoords.x;
		float hitY = row + GameVars.PPM_INV * (rect.height * 0.5f);

		editor.getCurrentLevel().addEntitySpawn(entityIndex, new Vector2(hitX, hitY), facingRight);
		
		return false;
	}

}
