package com.cpubrew.editor.mapobject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.game.GameVars;

public class SpriteRenderer implements MapObjectRenderer {

	private TextureRegion region;
	
	public SpriteRenderer(TextureRegion region) {
		this.region = region;
	}
	
	@Override
	public void render(SpriteBatch batch, Vector2 position) {
		float x = position.x;
		float y = position.y;
		
		float w = region.getRegionWidth();
		float h = region.getRegionHeight();
		
		batch.draw(region, x - w * 0.5f, y - h * 0.5f, w * 0.5f, h * 0.5f, w, h, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f);
	}

	@Override
	public void setAnimTime(float time) {
	}
	
	@Override
	public MapObjectRenderer createCopy() {
		return new SpriteRenderer(region);
	}
	
	public void setRegion(TextureRegion region) {
		this.region = region;
	}
	
	public TextureRegion getRegion() {
		return region;
	}

}
