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
	
//	public float getElapsed(AbilityType type){
//		return abilityMap.get(type).getTimeElapsed();
//	}
//	
//	public float getCooldown(AbilityType type){
//		return abilityMap.get(type).getCooldown();
//	}
//	
//	public TextureRegion getIcon(AbilityType type){
//		return abilityMap.get(type).getIcon();
//	}
//	
//	public boolean isReady(AbilityType type){
//		return abilityMap.get(type).isReady();
//	}
//	
//	public void addTime(AbilityType type, float amount){
//		abilityMap.get(type).addTime(amount);
//	}
//	
//	public void resetTime(AbilityType type){
//		abilityMap.get(type).elapsed = 0.0f;
//	}
	
	public ArrayMap<AbilityType, Ability> getAbilityMap(){
		return abilityMap;
	}
	
	@Override
	public void reset() {
		abilityMap = null;
	}
}