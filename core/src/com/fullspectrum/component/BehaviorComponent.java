package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.ai.AIBehavior;

public class BehaviorComponent implements Component, Poolable {

	public AIBehavior behavior;
	
	public BehaviorComponent set(AIBehavior behavior){
		this.behavior = behavior;
		return this;
	}
	
	@Override
	public void reset() {
		behavior = null;
	}
}