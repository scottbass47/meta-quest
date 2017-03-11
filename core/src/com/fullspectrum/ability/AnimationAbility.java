package com.fullspectrum.ability;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.input.Actions;

public abstract class AnimationAbility extends TimedAbility{
	
	protected Animation animation;
	
	public AnimationAbility(AbilityType type, TextureRegion icon, float cooldown, Actions input, Animation animation) {
		this(type, icon, cooldown, input, animation, false);
	}
	
	public AnimationAbility(AbilityType type, TextureRegion icon, float cooldown, Actions input, Animation animation, boolean isBlocking) {
		super(type, icon, cooldown, input, animation.getAnimationDuration(), isBlocking);
		this.animation = animation;
	}
}
