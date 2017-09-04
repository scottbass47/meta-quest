package com.cpubrew.ability;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cpubrew.input.Actions;

public abstract class AnimationAbility extends TimedAbility{
	
	protected Animation<TextureRegion> animation;
	
	public AnimationAbility(AbilityType type, TextureRegion icon, float cooldown, Actions input, Animation<TextureRegion> animation) {
		super(type, icon, cooldown, input, animation.getAnimationDuration(), true);
		this.animation = animation;
		lockFacing();
	}
}
