package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.ability.Ability;
import com.fullspectrum.ability.AbilityType;

public class AbilityComponent implements Component, Poolable{

	private ArrayMap<AbilityType, Ability> abilityMap;
	
	public AbilityComponent() {
		abilityMap = new ArrayMap<AbilityType, Ability>();
	}
	
	public AbilityComponent add(Ability ability){
		abilityMap.put(ability.getType(), ability);
		return this;
	}
	
	public Ability getAbility(AbilityType type){
		return abilityMap.get(type);
	}
	
	public ArrayMap<AbilityType, Ability> getAbilityMap(){
		return abilityMap;
	}
	
	public void lockAllBlocking(){
		for(Ability ability : abilityMap.values()) {
			if(ability.isBlocking()){
				ability.lock();
			}
		}
	}
	
	public void unlockAllBlocking(){
		for(Ability ability : abilityMap.values()) {
			if(ability.isBlocking()){
				ability.unlock();
			}
		}
	}
	
	@Override
	public void reset() {
		abilityMap = null;
	}
}