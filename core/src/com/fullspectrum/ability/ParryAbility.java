package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.input.Actions;

public class ParryAbility extends Ability{

	private float elapsed;
	private float maxTime;
	
	public ParryAbility(float cooldown, Actions input, float maxTime) {
		super(AbilityType.PARRY, Assets.getInstance().getHUDElement(Assets.PARRY_ICON), cooldown, input);
		this.maxTime = maxTime;
	}

	@Override
	public void init(Entity entity) {

	}

	@Override
	public void update(Entity entity, float delta) {
		elapsed += delta;
		
		// Nothing was blocked, return entity to idle state
		if(elapsed >= maxTime){
			setDone(true);
			Mappers.esm.get(entity).get(EntityStates.IDLING).changeState(EntityStates.IDLING);
		}
		
		
	}

	@Override
	public void destroy(Entity entity) {
		elapsed = 0.0f;
	}

}
