package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InvincibilityComponent implements Component, Poolable {

	public ObjectSet<InvincibilityType> types;
	
	public InvincibilityComponent() {
		types = new ObjectSet<InvincibilityType>();
	}
	
	public InvincibilityComponent add(InvincibilityType type){
		types.add(type);
		return this;
	}
	
	public InvincibilityComponent remove(InvincibilityType type){
		types.remove(type);
		return this;
	}
	
	public boolean isInvincible(Entity me, Entity other){
		for(InvincibilityType type : types){
			if(type.isInvincible(me, other)) return true;
		}
		return false;
	}
	
	@Override
	public void reset() {
		types = null;
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
