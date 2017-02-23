package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.fsm.transition.InputTransitionData;

public abstract class InstantAbility extends Ability{

	public InstantAbility(AbilityType type, TextureRegion icon, float cooldown, InputTransitionData inputData) {
		super(type, icon, cooldown, inputData);
	}
	
	/** Called once when the ability is used */
	public abstract void onUse(Entity entity);

	@Override
	public void init(Entity entity) {
		onUse(entity);
		setDone(true);
	}

	@Override
	public void update(Entity entity, float delta) {
	}

	@Override
	public void destroy(Entity entity) {
	}
}