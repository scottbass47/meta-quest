package com.fullspectrum.gui;

import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimatedLabel extends ImageLabel{

	private Animation animation;
	private float elapsed;
	
	public AnimatedLabel(Animation animation) {
		super(animation.getKeyFrame(0.0f));
	}
	
	public AnimatedLabel(Animation animation, float scale) {
		super(animation.getKeyFrame(0.0f), scale);
		this.animation = animation;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		elapsed += delta;
		setRegion(animation.getKeyFrame(elapsed));
	}
	
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
	public Animation getAnimation() {
		return animation;
	}

}
