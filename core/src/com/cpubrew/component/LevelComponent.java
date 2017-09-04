package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.level.Level;
import com.cpubrew.level.LevelHelper;

public class LevelComponent implements Component, Poolable{

	public Level level;
	public LevelHelper levelHelper;
	
	@Override
	public void reset() {
		level = null;
		levelHelper = null;
	}
	
	public LevelComponent set(Level level, Entity entity){
		this.level = level;
		levelHelper = new LevelHelper(level, entity);
		return this;
	}
	
}
