package com.fullspectrum.effects;

import com.badlogic.ashley.core.Entity;

public class PoisonDef extends EffectDef {

	private Entity fromEntity;
	private float duration;
	private float dps;
	
	public PoisonDef(Entity fromEntity, float duration, float dps) {
		super(EffectType.POISON);
		this.fromEntity = fromEntity;
		this.duration = duration;
		this.dps = dps;
	}

	@Override
	public void give(Entity toEntity) {
		Effects.givePoison(fromEntity, toEntity, duration, dps);
	}

	public Entity getFromEntity() {
		return fromEntity;
	}

	public void setFromEntity(Entity fromEntity) {
		this.fromEntity = fromEntity;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getDps() {
		return dps;
	}

	public void setDps(float dps) {
		this.dps = dps;
	}
	
	

}
