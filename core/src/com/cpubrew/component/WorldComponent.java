package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;

public class WorldComponent implements Component, Poolable{

	public World world;
	
	@Override
	public void reset() {
		world = null;
	}
	
	public WorldComponent set(World world){
		this.world = world;
		return this;
	}
	
}
