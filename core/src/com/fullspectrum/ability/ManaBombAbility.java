package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.fsm.transition.InputTransitionData;

public class ManaBombAbility extends InstantAbility{

	public ManaBombAbility(float cooldown, InputTransitionData inputData) {
		super(AbilityType.MANA_BOMB, Assets.getInstance().getSpriteAnimation(Assets.blueCoin).getKeyFrame(0.0f), cooldown, inputData);
	}

	@Override
	public void onUse(Entity entity) {
		ESMComponent esmComp = Mappers.esm.get(entity);
		esmComp.get(EntityStates.PROJECTILE_ATTACK).changeState(EntityStates.PROJECTILE_ATTACK);
	}
}