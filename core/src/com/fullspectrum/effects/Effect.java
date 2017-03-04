package com.fullspectrum.effects;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimeListener;

public abstract class Effect {

	protected float duration;
	protected Entity toEntity;
	
	public Effect(Entity toEntity, float duration){
		this.toEntity = toEntity;
		this.duration = duration;
	}
	
	public void apply(){
		// Don't apply the effect if its already applied
		if(Mappers.timer.get(toEntity).timers.containsKey(getName() + "_effect")) return;
		give();
		Mappers.timer.get(toEntity).add(getName() + "_effect", duration, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				cleanUp();
			}
		});
	}
	
	protected abstract void give();
	protected abstract void cleanUp();
	public abstract String getName();
	
	@Override
	public String toString() {
		return getName();
	}
}
