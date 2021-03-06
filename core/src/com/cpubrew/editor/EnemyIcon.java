package com.cpubrew.editor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.gui.Button;

public class EnemyIcon extends Button {

	private EntityIndex index;
	private float scale = 1.0f;
	private float elapsed = 0.0f;
	
	public EnemyIcon(EntityIndex index, float scale) {
		this.index = index;
		setScale(scale);
		setRenderBackground(false);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		elapsed += delta;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		Animation<TextureRegion> animation = index.getIdleAnimation();
		TextureRegion region = animation.getKeyFrame(elapsed);
		Rectangle rect = getBounds();
		
		float w = region.getRegionWidth();
		float h = region.getRegionHeight();
		float hw = w * 0.5f;
		float hh = h * 0.5f;
		
		batch.draw(region, rect.x + rect.width * 0.5f - hw, rect.y + rect.height * 0.5f - hh, hw, hh, w, h, scale, scale, 0.0f);
	}
	
	public EntityIndex getIndex() {
		return index;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		Rectangle hitbox = index.getHitBox();
		setSize((int)(hitbox.width * scale), (int)(hitbox.height * scale));
	}
}
