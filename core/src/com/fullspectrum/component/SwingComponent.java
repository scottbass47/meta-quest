package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.effects.EffectDef;
import com.fullspectrum.effects.EffectType;

public class SwingComponent implements Component, Poolable {

	public float rx;
	public float ry;
	public float startAngle = 0.0f;
	public float endAngle = 0.0f;
	public float delay;
	public float timeElapsed;
	public float damage;
	public float knockback;
	public boolean shouldSwing = false;
	public ObjectSet<EffectDef> effects;

	public SwingComponent() {
		effects = new ObjectSet<EffectDef>();
	}
	
	public SwingComponent set(float rx, float ry, float startAngle, float endAngle, float delay, float damage, float knockback) {
		this.rx = rx;
		this.ry = ry;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.delay = delay;
		this.damage = damage;
		this.knockback = knockback;
		return this;
	}
	
	public SwingComponent setEffects(ObjectSet<EffectDef> effects){
		this.effects = effects;
		return this;
	}
	
	/** If the effect is knockback, it is ignored. */
	public SwingComponent addEffect(EffectDef effect){
		if(effect.getType() == EffectType.KNOCKBACK) return this;
		effects.add(effect);
		return this;
	}

	@Override
	public void reset() {
		rx = 0.0f;
		ry = 0.0f;
		startAngle = 0.0f;
		endAngle = 0.0f;
		delay = 0.0f;
		timeElapsed = 0.0f;
		damage = 0.0f;
		knockback = 0.0f;
		effects = null;
		shouldSwing = false;
	}

}
