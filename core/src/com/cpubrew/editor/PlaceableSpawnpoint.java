package com.cpubrew.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.editor.command.Command;
import com.cpubrew.editor.command.PlaceSpawnpointCommand;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.game.GameVars;
import com.cpubrew.level.Level.EntitySpawn;
import com.cpubrew.utils.Maths;

public class PlaceableSpawnpoint implements Placeable{

	private EntityIndex entityIndex;
	private float animTime = 0.0f;
	private boolean facingRight = true;
	
	public PlaceableSpawnpoint(EntityIndex index) {
		this.entityIndex = index;
	}
	
	@Override
	public Command onClick(Vector2 mousePos, LevelEditor editor) {
		Rectangle rect = entityIndex.getHitBox();

		int row = Maths.toGridCoord(mousePos.y);
		
		float hitX = mousePos.x;
		float hitY = row + GameVars.PPM_INV * (rect.height * 0.5f);

		EntitySpawn spawn = new EntitySpawn(entityIndex, new Vector2(hitX, hitY), facingRight);
		return new PlaceSpawnpointCommand(spawn);
	}

	@Override
	public void update(float delta) {
		animTime += delta;
	}
	
	@Override
	public void render(Vector2 mousePos, SpriteBatch batch, LevelEditor editor) {
		// We handle input in the render method because update doesn't get called frequently enough to
		/// catch just pressed keyboard events
		if(Gdx.input.isKeyJustPressed(Keys.R)) {
			facingRight = !facingRight;
		}

		float x = mousePos.x;
		float y = mousePos.y;
		
		int row = Maths.toGridCoord(y);
		
		Animation<TextureRegion> idle = entityIndex.getIdleAnimation();
		Rectangle rect = entityIndex.getHitBox();
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
		
		if(!facingRight) {
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
				if(editor.contains(row, col) && editor.getTile(row, col) != null && editor.getTile(row, col).isSolid()) return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean placeOnClick() {
		return true;
	}
}
