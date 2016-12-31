package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.AbilityType;

public class AbilitySystem extends IteratingSystem {

	public AbilitySystem() {
		super(Family.all(AbilityComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		AbilityComponent abilityComp = Mappers.ability.get(entity);
		for(AbilityType type : abilityComp.getAbilityMap().keys()){
			abilityComp.addTime(type, deltaTime);
		}
	}

}
