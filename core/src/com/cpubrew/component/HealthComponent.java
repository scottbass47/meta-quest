package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class HealthComponent implements Component, Poolable{

	public float maxHealth = 0.0f;
	public float health = 0.0f;
	
	public HealthComponent set(float maxHealth, float health){
		this.maxHealth = maxHealth;
		this.health = health;
		return this;
	}
	
	@Override
	public void reset() {
		maxHealth = 0.0f;
		health = 0.0f;
	}
	
}
