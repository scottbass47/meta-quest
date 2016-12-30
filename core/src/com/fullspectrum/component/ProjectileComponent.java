package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ProjectileComponent implements Component, Poolable {

	public float x;
	public float y;
	public float speed;
	public float angle;
	public boolean isArc;
	
	public ProjectileComponent set(float x, float y, float speed, float angle, boolean isArc){
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.angle = angle;
		this.isArc = isArc;
		return this;
	}
	
	@Override
	public void reset() {
		x = 0.0f;
		y = 0.0f;
		speed = 0.0f;
		angle = 0.0f;
		isArc = false;
	}

}
