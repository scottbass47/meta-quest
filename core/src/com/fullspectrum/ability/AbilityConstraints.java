package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;

public interface AbilityConstraints {

	public boolean canUse(Ability ability, Entity entity);
	
}
