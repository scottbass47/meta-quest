package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.level.Level;

public class LevelComponent implements Component, Poolable{

	public Level level;
	
	@Override
	public void reset() {
		level = null;
	}
	
	public LevelComponent set(Level level){
		this.level = level;
		return this;
	}
	
}
