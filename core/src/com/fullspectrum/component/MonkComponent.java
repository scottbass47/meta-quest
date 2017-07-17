package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.effects.EffectDef;

public class MonkComponent implements Component, Poolable {

	public EffectDef activeEffect = null;
	public boolean swingUp = false;
	public boolean canDash = true;
	public float dashElapsed = 2.0f;
	
	@Override
	public void reset() {
		activeEffect = null;
		swingUp = false;
		canDash = true;
		dashElapsed = 2.0f;
	}

}
