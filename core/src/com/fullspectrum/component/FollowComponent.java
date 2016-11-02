package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class FollowComponent implements Component, Poolable{

	public Entity toFollow;
	
	@Override
	public void reset() {
		toFollow = null;
	}
	
	public FollowComponent set(Entity toFollow){
		this.toFollow = toFollow;
		return this;
	}
	
}
