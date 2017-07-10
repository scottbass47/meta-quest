package com.fullspectrum.gui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedLabel extends ImageLabel{

	private Animation<TextureRegion> animation;
	private float elapsed;
	
	public AnimatedLabel(Animation<TextureRegion> animation) {
		super(animation.getKeyFrame(0.0f));
	}
	
	public AnimatedLabel(Animation<TextureRegion> animation, float scale) {
		super(animation.getKeyFrame(0.0f), scale);
		this.animation = animation;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		elapsed += delta;
		setRegion(animation.getKeyFrame(elapsed));
	}
	
	public void setAnimation(Animation<TextureRegion> animation) {
		this.animation = animation;
	}
	
	public Animation<TextureRegion> getAnimation() {
		return animation;
	}

}
