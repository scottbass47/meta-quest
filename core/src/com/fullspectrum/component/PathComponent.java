package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.ai.PathFinder;

public class PathComponent implements Component, Poolable{

	public PathFinder pathFinder;
	
	@Override
	public void reset() {
		pathFinder = null;
	}
	
	public PathComponent set(PathFinder pathFinder){
		this.pathFinder = pathFinder;
		return this;
	}
	
}
