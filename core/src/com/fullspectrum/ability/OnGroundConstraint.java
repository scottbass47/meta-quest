package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.Mappers;

public class OnGroundConstraint implements AbilityConstraints{

	@Override
	public boolean canUse(Ability ability, Entity entity) {
		return Mappers.collision.get(entity).onGround();
	}

}
