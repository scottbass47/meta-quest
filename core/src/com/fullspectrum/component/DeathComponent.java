package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class DeathComponent implements Component, Poolable {

	public DeathBehavior onDeath;
	public boolean triggered = false;
	
	public DeathComponent set(DeathBehavior onDeath){
		this.onDeath = onDeath;
		return this;
	}
	
	@Override
	public void reset() {
		onDeath = null;
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
