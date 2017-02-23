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
			if(ability.isReady() && checkInput(ability.getInputData(), inputComp.input)){
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
	
	// CLEANUP Duplicate
	private boolean checkInput(InputTransitionData itd, Input input) {
		int counter = 0;
		for (InputTrigger trigger : itd.triggers) {
			boolean triggered = false;
			// If its a game input, it must be past the analog threshold to be considered an action
			if(input instanceof GameInput){
				triggered = trigger.justPressed ? input.isJustPressed(trigger.action) : input.getValue(trigger.action) > GameInput.ANALOG_THRESHOLD;
			}
			else{
				triggered = trigger.justPressed ? input.isJustPressed(trigger.action) : input.isPressed(trigger.action);
			}
			triggered = (triggered && itd.pressed) || (!triggered && !itd.pressed);
			if (triggered && itd.type == InputTransitionData.Type.ANY_ONE) return true;
			if (triggered) counter++;
		}
		switch (itd.type) {
		case ANY_ONE:
			return false;
		case ONLY_ONE:
			return counter == 1;
		case ALL:
			return counter == itd.triggers.size;
		default:
			return false;
		}
	}

}
