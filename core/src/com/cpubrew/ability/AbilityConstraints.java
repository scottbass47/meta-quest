package com.cpubrew.ability;

import com.badlogic.ashley.core.Entity;

public interface AbilityConstraints {

	public boolean canUse(Ability ability, Entity entity);
	
}
