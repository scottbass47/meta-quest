package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class DeathComponent implements Component, Poolable {

	public DeathBehavior onDeath;
	private boolean triggered = false;
	private boolean shouldDie = false;
	
	public DeathComponent set(DeathBehavior onDeath){
		this.onDeath = onDeath;
		return this;
	}
	
	@Override
	public void reset() {
		onDeath = null;
		triggered = false;
	}
	
	public void triggerDeath(){
		if(!triggered){
			triggered = true;
			shouldDie = true;
		}
	}
	
	public boolean shouldDie(){
		return shouldDie;
	}
	
	public void makeDead(){
		shouldDie = false;
	}
	
	public void makeAlive(){
		shouldDie = false;
		triggered = false;
	}
	
	public static interface DeathBehavior{
		public void onDeath(Entity entity);
	}

	public static class DefaultDeathBehavior implements DeathBehavior{
		@Override
		public void onDeath(Entity entity) {
			entity.add(Mappers.engine.get(entity).engine.createComponent(RemoveComponent.class));
		}
	}
}
