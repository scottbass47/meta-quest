package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.Level.EntitySpawn;
import com.fullspectrum.utils.Maths;

public class SelectableSpawnpoint implements Selectable<EntitySpawn>{

	private EntitySpawn spawn;
	private float animTime;
	
	public SelectableSpawnpoint(EntitySpawn spawn) {
		this.spawn = spawn;
	}
	
	@Override
	public void update(float delta, LevelEditor editor) {
		animTime += 0.0f;
	}

	@Override
	public void render(SpriteBatch batch, Vector2 mousePos, LevelEditor editor) {
		float x = mousePos.x;
		float y = mousePos.y;
		
		int row = Maths.toGridCoord(y);
		
		Animation<TextureRegion> idle = spawn.getIndex().getIdleAnimation();
		Rectangle rect = spawn.getIndex().getHitBox();
		TextureRegion region = idle.getKeyFrame(animTime);
		float w = region.getRegionWidth();
		float h = region.getRegionHeight();
		
		float adjustedY = row + GameVars.PPM_INV * (rect.height * 0.5f);
		float yy =  adjustedY - h * 0.5f;
		
		float hitX = x - GameVars.PPM_INV * (rect.width * 0.5f);
		float hitY = yy + h * 0.5f - GameVars.PPM_INV * (rect.height * 0.5f);
		
		if(collidingWithMap(hitX, hitY, GameVars.PPM_INV * rect.width, GameVars.PPM_INV * rect.height, editor)){
			batch.setColor(Color.RED);
		} 
		
		if(!spawn.isFacingRight()) {
			region.flip(true, false);
		}
		
		batch.draw(region, x - w * 0.5f, yy, w * 0.5f, h * 0.5f, w, h, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f);

		region.flip(region.isFlipX(), false);
		batch.setColor(Color.WHITE);
	}

	private boolean collidingWithMap(float x, float y, float width, float height, LevelEditor editor) {
		int minRow = Math.abs(y - (int) y) < 0.0005f ? (int)y : Maths.toGridCoord(y);
		int minCol = Maths.toGridCoord(x);
		int maxRow = Maths.toGridCoord(y + height);
		int maxCol = Maths.toGridCoord(x + width);
		
		for(int row = minRow; row <= maxRow; row++) {
			for(int col = minCol; col <= maxCol; col++) {
				if(editor.getTile(row, col) != null && editor.getTile(row, col).isSolid()) return true;
			}
		}
		return false;
	}
	@Override
	public Selectable<EntitySpawn> copy(LevelEditor editor) {
		EntitySpawn copy = new EntitySpawn(spawn.getIndex(), spawn.getPos(), spawn.isFacingRight());
		return new SelectableSpawnpoint(copy);
	}

	@Override
	public void remove(LevelEditor editor) {
		editor.getCurrentLevel().removeSpawn(spawn);
	}

	@Override
	public Vector2 getPosition(Vector2 offset) {
		return new Vector2(spawn.getPos().x + offset.x, spawn.getPos().y + offset.y);
	}

	@Override
	public boolean contentsEqual(EntitySpawn value) {
		return spawn.equals(value);
	}

	@Override
	public void move(Vector2 position, LevelEditor editor) {
		Level level = editor.getCurrentLevel();
		Array<EntitySpawn> spawns = level.getEntitySpawns();
		
		EntityIndex entityIndex = spawn.getIndex();
		Rectangle rect = entityIndex.getHitBox();

		int row = Maths.toGridCoord(position.y);
		
		float hitX = position.x;
		float hitY = row + GameVars.PPM_INV * (rect.height * 0.5f);

		spawn.setPos(new Vector2(hitX, hitY));
		spawns.add(spawn);
	}

}
