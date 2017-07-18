package com.fullspectrum.effects;

import com.badlogic.ashley.core.Entity;

public abstract class EffectDef {

	private EffectType type;
	
	public EffectDef(EffectType type){
		this.type = type;
	}
	
	public EffectType getType() {
		return type;
	}
	
	public abstract void give(Entity toEntity);
}