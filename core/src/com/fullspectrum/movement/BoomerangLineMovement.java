package com.fullspectrum.movement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class BoomerangLineMovement implements Movement{

	private float speed;
	private float angle;
	
	public BoomerangLineMovement(){
		this.speed = 0.0f;
		this.angle = 0.0f;
	}
	
	public BoomerangLineMovement(float speed, float angle){
		this.speed = speed;
		this.angle = angle;
	}
	
	@Override
	public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
		return new Vector2(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle));
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	
	
}
