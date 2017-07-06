package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InvincibilityComponent implements Component, Poolable {

	private ArrayMap<InvincibilityType, Integer> iMap;
	
	public InvincibilityComponent() {
		iMap = new ArrayMap<InvincibilityType, Integer>();
	}
	
	public InvincibilityComponent add(InvincibilityType type){
		if(!iMap.containsKey(type)) iMap.put(type, 0);
		iMap.put(type, iMap.get(type) + 1);
		return this;
	}
	
	public InvincibilityComponent remove(InvincibilityType type){
		if(!iMap.containsKey(type) || iMap.get(type) <= 0) return this;
		iMap.put(type, iMap.get(type) - 1);
		return this;
	}
	
	public InvincibilityComponent addAll(InvincibilityType... types){
		for(InvincibilityType type : types) add(type);
		return this;
	}
	
	public InvincibilityComponent addAll(ObjectSet<InvincibilityType> types){
		for(InvincibilityType type : types) add(type);
		return this;
	}
	
	public InvincibilityComponent removeAll(InvincibilityType... types){
		for(InvincibilityType type : types) remove(type);
		return this;
	}
	
	public InvincibilityComponent removeAll(ObjectSet<InvincibilityType> types){
		for(InvincibilityType type : types) remove(type);
		return this;
	}
	
	public void clear() {
		for(InvincibilityType type : iMap.keys()) {
			iMap.put(type, 0);
		}
	}
	
	public boolean isInvincible(Entity me, Entity other){
		for(InvincibilityType type : iMap.keys()){
			if(iMap.get(type) > 0 && type.isInvincible(me, other)) return true;
		}
		return false;
	}
	
	@Override
	public void reset() {
		iMap = null;
	}
	
	public enum InvincibilityType{
		ALL{
			@Override
			public boolean isInvincible(Entity me, Entity other) {
				return true;
			}
		},
		PROJECTILE{
			@Override
			public boolean isInvincible(Entity me, Entity other) {
				return Mappers.projectile.get(other) != null;
			}
		};
		
		public abstract boolean isInvincible(Entity me, Entity other);
	}

}
