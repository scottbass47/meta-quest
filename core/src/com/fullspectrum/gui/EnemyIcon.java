package com.fullspectrum.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.fullspectrum.entity.EntityIndex;

public class EnemyIcon extends Button {

	private EntityIndex index;
	private float scale = 1.0f;
	private float elapsed = 0.0f;
	
	public EnemyIcon(EntityIndex index, float scale) {
		this.index = index;
		setScale(scale);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		elapsed += delta;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		Animation animation = index.getIdleAnimation();
		TextureRegion region = animation.getKeyFrame(elapsed);
		java.awt.Rectangle rect = getBounds();
		
		float w = region.getRegionWidth();
		float h = region.getRegionHeight();
		float hw = w * 0.5f;
		float hh = h * 0.5f;
		
		if(index == EntityIndex.AI_PLAYER) batch.setColor(Color.RED);
		batch.draw(region, rect.x + rect.width * 0.5f - hw, rect.y + rect.height * 0.5f - hh, hw, hh, w, h, scale, scale, 0.0f);
		batch.setColor(Color.WHITE);
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
