package com.fullspectrum.editor.action;

import java.util.Iterator;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.Level.EntitySpawn;
import com.fullspectrum.utils.Maths;

public class SelectAction extends Action{
	
	private ShapeRenderer shape;
	private Array<EntitySpawn> selectedSpawnPoints;
	private Vector2 startCoords;
	private Vector2 currentCoords;
	private boolean mouseDown = false;
	
	public SelectAction() {
		startCoords = new Vector2();
		currentCoords = new Vector2();
		shape = new ShapeRenderer();
		selectedSpawnPoints = new Array<EntitySpawn>();
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch) {
		if(mouseDown) {
			shape.setProjectionMatrix(worldCamera.combined);
			shape.begin(ShapeType.Line);
			shape.setColor(Color.BLACK);
			shape.rect(startCoords.x, startCoords.y, currentCoords.x - startCoords.x, currentCoords.y - startCoords.y);
			shape.end();
		}
	}
	
	public boolean isSelected(EntitySpawn spawn) {
		return selectedSpawnPoints.contains(spawn, false);
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.DEL) {
			for(Iterator<EntitySpawn> iter = selectedSpawnPoints.iterator(); iter.hasNext(); ) {
				EntitySpawn spawn = iter.next();
				Level level = editor.getCurrentLevel();
				level.removeSpawn(spawn);
				iter.remove();
			}
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		currentCoords.set(editor.toWorldCoords(editor.toHudCoords(screenX, screenY)));
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 coords = editor.toHudCoords(screenX, screenY);
		startCoords = editor.toWorldCoords(coords.x, coords.y);
		currentCoords.set(startCoords);
		selectedSpawnPoints.clear();
		mouseDown = true;
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector2 hudCoords = editor.toHudCoords(screenX, screenY);
		Vector2 endCoords = editor.toWorldCoords(hudCoords.x, hudCoords.y);
		
		Rectangle selectRect = new Rectangle(
				Math.min(startCoords.x, endCoords.x), 
				Math.min(startCoords.y, endCoords.y), 
				Math.abs(endCoords.x - startCoords.x),
				Math.abs(endCoords.y - startCoords.y)
		);
		
		for(EntitySpawn spawn : editor.getCurrentLevel().getEntitySpawns()) {
			Vector2 spawnPoint = spawn.getPos();
			EntityIndex index = spawn.getIndex();
			Rectangle hitbox = Maths.scl(index.getHitBox(), GameVars.PPM_INV);
			
			float lowerX = spawnPoint.x - hitbox.width * 0.5f;
			float lowerY = spawnPoint.y - hitbox.height * 0.5f;
			
			Rectangle collision = new Rectangle(lowerX, lowerY, hitbox.width, hitbox.height);
			
			if(selectRect.overlaps(collision)) {
				if(!selectedSpawnPoints.contains(spawn, false)) selectedSpawnPoints.add(spawn);
			}
		}
		mouseDown = false;
		return false;
	}

}
