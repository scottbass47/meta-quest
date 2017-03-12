package com.fullspectrum.effects;

import com.badlogic.ashley.core.Entity;

public class KnockBackDef extends EffectDef{

	private float distance;
	private float angle;
	
	public KnockBackDef(float distance, float angle) {
		super(EffectType.KNOCKBACK);
		this.distance = distance;
		this.angle = angle;
	}

	@Override
	public void give(Entity toEntity) {
		Effects.giveKnockBack(toEntity, distance, angle);
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public float getAngle() {
		return angle;
	}
	
}
