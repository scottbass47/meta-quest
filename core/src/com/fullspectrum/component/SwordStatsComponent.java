package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;

public class SwordStatsComponent implements Component, Poolable{

//	public ObjectSet<Entity> hitEntities;
	public int damage = 0;
	
//	public SwordStatsComponent() {
//		hitEntities = new ObjectSet<Entity>();
//	}
	
	public SwordStatsComponent set(int damage){
		this.damage = damage;
		return this;
	}
	
	@Override
	public void reset() {
//		hitEntities = null;
		damage = 0;
	}

}
