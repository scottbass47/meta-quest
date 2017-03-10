package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.ability.Ability;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTrigger;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.input.Input;

public class AbilitySystem extends IteratingSystem {

	public AbilitySystem() {
		super(Family.all(AbilityComponent.class, InputComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		AbilityComponent abilityComp = Mappers.ability.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);
		
		for(AbilityType type : abilityComp.getAbilityMap().keys()){
			Ability ability = abilityComp.getAbility(type);
			if(!ability.isLocked()){
				ability.addTime(deltaTime);
			}
			
			// Trigger the ability
			if(ability.isReady() && inputComp.input.isJustPressed(ability.getInput())){
				ability.init(entity);
				ability.resetTimeElapsed();
				ability.lock();
			}
			
			if(ability.isDone()){
				ability.destroy(entity);
				ability.setDone(false);
				ability.unlock();
			}else if(ability.isLocked()){
				ability.update(entity, deltaTime);
			}
		}
	}
}
