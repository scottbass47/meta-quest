package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TypeComponent implements Component, Poolable{

	public EntityType type = EntityType.NEUTRAL;
	public ObjectSet<EntityType> collideWith;
	
	public TypeComponent(){
		collideWith = new ObjectSet<EntityType>();
	}
	
	public TypeComponent set(EntityType type){
		this.type = type;
		return this;
	}
	
	public TypeComponent setCollideWith(ObjectSet<EntityType> collideWith){
		this.collideWith = collideWith;
		return this;
	}
	
	public TypeComponent setCollideWith(EntityType... types){
		collideWith.clear();
		collideWith.addAll(types);
		return this;
	}
	
	public boolean shouldCollide(TypeComponent typeComp){
		if(typeComp == null) return false;
		return shouldCollide(typeComp.type);
	}
	
	public boolean shouldCollide(EntityType type){
		if(type == null) return false;
		return collideWith.contains(type);
	}
	
	public boolean same(TypeComponent typeComp){
		if(typeComp == null) return false;
		return same(typeComp.type);
	}
	
	public boolean same(EntityType type){
		if(type == null) return false;
		return this.type.equals(type);
	}
	
	@Override
	public void reset() {
		type = EntityType.NEUTRAL;
		collideWith = null;
	}
	
	public enum EntityType{
		FRIENDLY {
			@Override
			public EntityType getOpposite() {
				return ENEMY;
			}
		},
		ENEMY {
			@Override
			public EntityType getOpposite() {
				return FRIENDLY;
			}
		},
		NEUTRAL {
			@Override
			public EntityType getOpposite() {
				return NEUTRAL;
			}
		};

		public abstract EntityType getOpposite();
	}
}