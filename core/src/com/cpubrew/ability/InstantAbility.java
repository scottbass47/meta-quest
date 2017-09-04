package com.cpubrew.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cpubrew.input.Actions;

public abstract class InstantAbility extends Ability{

	public InstantAbility(AbilityType type, TextureRegion icon, float cooldown, Actions input) {
		this(type, icon, cooldown, input, false);
	}
	
	public InstantAbility(AbilityType type, TextureRegion icon, float cooldown, Actions input, boolean isBlocking) {
		super(type, icon, cooldown, input, isBlocking);
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