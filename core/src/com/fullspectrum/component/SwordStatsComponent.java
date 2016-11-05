package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class SwordStatsComponent implements Component, Poolable{

	public Array<Entity> hitEntities;
	public int damage = 0;
	
	public SwordStatsComponent() {
		hitEntities = new Array<Entity>();
	}
	
	public SwordStatsComponent set(int damage){
		this.damage = damage;
		return this;
	}
	
	@Override
	public void reset() {
		hitEntities = null;
		damage = 0;
	}

}
