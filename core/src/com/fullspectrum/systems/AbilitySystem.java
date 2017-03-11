package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.ability.Ability;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;

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
			if(!ability.isActivated()) continue;
			
			if(!ability.inUse() && !ability.isReady()){
				ability.addTime(deltaTime);
			}
			
			// Trigger the ability
			if(ability.isReady() && inputComp.input.isJustPressed(ability.getInput()) && ability.canUse(entity)){
				ability.init(entity);
				ability.resetTimeElapsed();
				ability.setInUse(true);
				// If ability is blocking, lock other blocking abilities
				if(ability.isBlocking()){
					for(int i = 0; i < abilityComp.getAbilityMap().size; i++){
						Ability ab = abilityComp.getAbilityMap().getValueAt(i);
						if(ab.isBlocking()) ab.lock();
					}
				}
			}
			
			if(ability.isDone()){
				ability.destroy(entity);
				ability.setDone(false);
				ability.setInUse(false);
				//If ability is blocking, unlock other blocking abilities
				if(ability.isBlocking()){
					for(int i = 0; i < abilityComp.getAbilityMap().size; i++){
						Ability ab = abilityComp.getAbilityMap().getValueAt(i);
						if(ab.isBlocking()) ab.unlock();
					}
				}
			}else if(ability.inUse()){
				ability.update(entity, deltaTime);
			}
		}
	}
}
