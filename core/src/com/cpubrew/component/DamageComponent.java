package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class DamageComponent implements Component, Poolable{

	public float damage;
	
	public DamageComponent set(float damage){
		this.damage = damage;
		return this;
	}
	
	@Override
	public void reset() {
		damage = 0.0f;
	}

}
