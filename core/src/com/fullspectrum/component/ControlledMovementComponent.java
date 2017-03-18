package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ControlledMovementComponent implements Component, Poolable{

	public Movement movement;
	
	public ControlledMovementComponent set(Movement movement){
		this.movement = movement;
		return this;
	}
	
	@Override
	public void reset() {
		movement = null;
	}
	
	public static interface Movement {
		public Vector2 getSpeed(Entity entity, float delta);
	}
}
