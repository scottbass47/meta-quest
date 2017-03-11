package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.input.Actions;

public abstract class TimedAbility extends Ability{

	protected float duration;
	protected float elapsed;
	
	
	public TimedAbility(AbilityType type, TextureRegion icon, float cooldown, Actions input, float duration) {
		this(type, icon, cooldown, input, duration, false);
	}

	public TimedAbility(AbilityType type, TextureRegion icon, float cooldown, Actions input, float duration, boolean isBlocking) {
		super(type, icon, cooldown, input, isBlocking);
		this.duration = duration;
	}

	/** Called once per update */ 
	public abstract void onUpdate(Entity entity, float delta);
	
	@Override
	public void update(Entity entity, float delta) {
		onUpdate(entity, delta);
		elapsed += delta;
		if(elapsed >= duration){
			setDone(true);
			elapsed = 0.0f;
		}
	}
	
}
