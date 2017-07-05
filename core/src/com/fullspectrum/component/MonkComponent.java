package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.effects.EffectDef;

public class MonkComponent implements Component, Poolable {

	public EffectDef activeEffect = null;
	public boolean swingUp = false;
	
	@Override
	public void reset() {
		activeEffect = null;
		swingUp = false;
	}

}
