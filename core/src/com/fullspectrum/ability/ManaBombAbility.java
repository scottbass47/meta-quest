package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.input.Actions;

public class ManaBombAbility extends InstantAbility{

	public ManaBombAbility(float cooldown, Actions input) {
		super(AbilityType.MANA_BOMB, Assets.getInstance().getSpriteAnimation(Assets.blueCoin).getKeyFrame(0.0f), cooldown, input, true);
	}

	@Override
	public void onUse(Entity entity) {
		// CLEANUP Move ability's logic out of state machine and into here
		ESMComponent esmComp = Mappers.esm.get(entity);
		esmComp.get(EntityStates.PROJECTILE_ATTACK).changeState(EntityStates.PROJECTILE_ATTACK);
	}
}