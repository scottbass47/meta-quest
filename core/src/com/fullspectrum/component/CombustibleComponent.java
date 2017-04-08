package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CombustibleComponent implements Poolable, Component {

	public ObjectSet<Entity> hitEntities;
	public float radius;
	public float speed;
	public float damage;
	public float dropOffRate; // damage lost per meter traveled
	public float knockback;
	public boolean shouldExplode = false;
	
	public CombustibleComponent(){
		hitEntities = new ObjectSet<Entity>();
	}
	
	public CombustibleComponent set(float radius, float speed, float damage, float dropOffRate, float knockback){
		this.radius = radius;
		this.speed = speed;
		this.damage = damage;
		this.dropOffRate = dropOffRate;
		this.knockback = knockback;
		return this;
	}

	@Override
	public void reset() {
		hitEntities = null;
		radius = 0.0f;
		speed = 0.0f;
		damage = 0.0f;
		dropOffRate = 0.0f;
		knockback = 0.0f;
		shouldExplode = false;
	}

}
