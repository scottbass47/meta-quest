package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.AbilityType;

public class AbilityComponent implements Component, Poolable{

	private ArrayMap<AbilityType, Ability> abilityMap;
	
	public AbilityComponent() {
		abilityMap = new ArrayMap<AbilityType, Ability>();
	}

	public AbilityComponent add(AbilityType type, TextureRegion icon, float rechargeTime){
		abilityMap.put(type, new Ability(icon, rechargeTime));
		return this;
	}
	
	public float getElapsed(AbilityType type){
		return abilityMap.get(type).elapsed;
	}
	
	public float getRechargeTime(AbilityType type){
		return abilityMap.get(type).rechargeTime;
	}
	
	public TextureRegion getIcon(AbilityType type){
		return abilityMap.get(type).icon;
	}
	
	public boolean isAbilityReady(AbilityType type){
		return abilityMap.get(type).elapsed >= abilityMap.get(type).rechargeTime;
	}
	
	public void addTime(AbilityType type, float amount){
		abilityMap.get(type).elapsed += amount;
	}
	
	public void resetTime(AbilityType type){
		abilityMap.get(type).elapsed = 0.0f;
	}
	
	public ArrayMap<AbilityType, Ability> getAbilityMap(){
		return abilityMap;
	}
	
	@Override
	public void reset() {
		abilityMap = null;
	}
	
	public static class Ability{
		
		private TextureRegion icon;
		private float rechargeTime;
		private float elapsed = 0.0f;
		private float time;
		
		public Ability(TextureRegion icon, float rechargeTIme){
			this.icon = icon;
			this.rechargeTime = rechargeTIme;
		}
		
	}
	
	public static interface AbilityBehavior{
		
	}

}
