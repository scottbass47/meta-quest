package com.cpubrew.effects;

import com.badlogic.ashley.core.Entity;

public class KnockBackDef extends EffectDef{

	private Entity fromEntity;
	private float distance;
	private float angle;
	
	public KnockBackDef(float distance, float angle) {
		super(EffectType.KNOCKBACK);
		this.distance = distance;
		this.angle = angle;
	}
	
	public KnockBackDef(Entity fromEntity, float distance, float angle) {
		super(EffectType.KNOCKBACK);
		this.fromEntity = fromEntity;
		this.distance = distance;
		this.angle = angle;
	}
	
	/**
	 * If <code>fromEntity</code> was specified, <code>giveKnockBackWithFlip</code> is used, otherwise
	 * <code>giveKnockBack</code> is called.
	 */
	@Override
	public void give(Entity toEntity) {
		if(fromEntity != null) {
			Effects.giveKnockBackWithFlip(fromEntity, toEntity, distance, angle);
		} else {
			Effects.giveKnockBack(toEntity, distance, angle);
		}
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
