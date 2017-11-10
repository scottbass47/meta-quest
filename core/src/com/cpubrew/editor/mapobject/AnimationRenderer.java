package com.cpubrew.editor.mapobject;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class AnimationRenderer implements MapObjectRenderer {

	private float time;
	private SpriteRenderer spriter;
	private Animation<TextureRegion> animation;
	
	public AnimationRenderer(Animation<TextureRegion> animation) {
		this.animation = animation;
		spriter = new SpriteRenderer(null);
	}
	
	@Override
	public void render(SpriteBatch batch, Vector2 position) {
		spriter.setRegion(animation.getKeyFrame(time));
		spriter.render(batch, position);
	}

	@Override
	public void setAnimTime(float time) {
		this.time = time;
	}
	
	@Override
	public MapObjectRenderer createCopy() {
		return new AnimationRenderer(animation);
	}
	
	public void setAnimation(Animation<TextureRegion> animation) {
		this.animation = animation;
	}
	
	public Animation<TextureRegion> getAnimation() {
		return animation;
	}


}
