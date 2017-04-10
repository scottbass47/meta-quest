package com.fullspectrum.movement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class BoomerangCurveMovement implements Movement{

	private Entity parent;
	private float speed;
	private float turnSpeed;
	private boolean right;
	private float currentAngle;
	
	public BoomerangCurveMovement(Entity parent, float speed, float turnSpeed, boolean right){
		this.parent = parent;
		this.speed = speed;
		this.turnSpeed = turnSpeed;
		this.right = right;
	}
	
	@Override
	public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
		Vector2 vec = new Vector2();
		if(right){
			currentAngle -= turnSpeed * delta;
		} else {
			currentAngle += turnSpeed * delta;
		}
		vec.set(speed * MathUtils.cosDeg(currentAngle), speed * MathUtils.sinDeg(currentAngle));
		return vec;
	}
	
	public void setParent(Entity parent) {
		this.parent = parent;
	}
	
	public Entity getParent() {
		return parent;
	}
	
	public void setTurnSpeed(float turnSpeed) {
		this.turnSpeed = turnSpeed;
	}
	
	public float getTurnSpeed() {
		return turnSpeed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getSpeed() {
		return speed;
	}

	public float getCurrentAngle() {
		return currentAngle;
	}

	public void setCurrentAngle(float currentAngle) {
		this.currentAngle = currentAngle;
	}
	
	
	
}
