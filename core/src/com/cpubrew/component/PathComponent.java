package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.ai.PathFinder;

public class PathComponent implements Component, Poolable{

	public PathFinder pathFinder;
	public boolean shouldFollow = false;
	
	@Override
	public void reset() {
		pathFinder = null;
		shouldFollow = false;
	}
	
	public PathComponent set(PathFinder pathFinder){
		this.pathFinder = pathFinder;
		return this;
	}
	
}
