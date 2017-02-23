package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.fsm.transition.InputTransitionData;

public abstract class TimedAbility extends Ability{

	private float time;
	private float elapsed;
	
	public TimedAbility(AbilityType type, TextureRegion icon, float cooldown, InputTransitionData inputData, float time) {
		super(type, icon, cooldown, inputData);
		this.time = time;
	}

	/** Called once per update */ 
	public abstract void onUpdate(Entity entity, float delta);
	
	@Override
	public void update(Entity entity, float delta) {
		onUpdate(entity, delta);
		elapsed += delta;
		if(elapsed >= time){
			setDone(true);
			elapsed = 0.0f;
		}
	}
	
}
