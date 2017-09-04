package com.cpubrew.effects;

import com.badlogic.ashley.core.Entity;

public class StunDef extends EffectDef{

	private float duration;
	
	public StunDef(float duration) {
		super(EffectType.STUN);
		this.duration = duration;
	}
	
	public void setDuration(float duration) {
		this.duration = duration;
	}
	
	public float getDuration() {
		return duration;
	}

	@Override
	public void give(Entity toEntity) {
		Effects.giveStun(toEntity, duration);
	}
}
