package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.EntityStatus;

public class StatusComponent implements Component, Poolable{

	public EntityStatus status = EntityStatus.NEUTRAL;
	public ObjectSet<EntityStatus> collideWith;
	
	public StatusComponent(){
		collideWith = new ObjectSet<EntityStatus>();
	}
	
	public StatusComponent set(EntityStatus type){
		this.status = type;
		return this;
	}
	
	public StatusComponent setCollideWith(ObjectSet<EntityStatus> collideWith){
		this.collideWith = collideWith;
		return this;
	}
	
	public StatusComponent setCollideWith(EntityStatus... status){
		collideWith.clear();
		collideWith.addAll(status);
		return this;
	}
	
	public boolean shouldCollide(StatusComponent statusComp){
		if(statusComp == null) return true;
		return shouldCollide(statusComp.status);
	}
	
	public boolean shouldCollide(EntityStatus status){
		if(status == null) return true;
		return collideWith.contains(status);
	}
	
	public boolean same(StatusComponent statusComp){
		if(statusComp == null) return false;
		return same(statusComp.status);
	}
	
	public boolean same(EntityStatus status){
		if(status == null) return false;
		return this.status.equals(status);
	}
	
	@Override
	public void reset() {
		status = EntityStatus.NEUTRAL;
		collideWith = null;
	}
}