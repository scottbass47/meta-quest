package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TargetComponent implements Component, Poolable{

	public Entity target;

	@Override
	public void reset() {
		target = null;
	}
	
	public TargetComponent set(Entity target){
		this.target = target;
		return this;
	}
	
}
