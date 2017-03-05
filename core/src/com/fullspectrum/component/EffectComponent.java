package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.effects.Effect;

public class EffectComponent implements Component, Poolable{

	public Array<Effect> effects;
	
	public EffectComponent(){
		effects = new Array<Effect>();
	}
	
	public EffectComponent add(Effect effect){
		effects.add(effect);
		return this;
	}
	
	public Effect remove(Effect effect){
		return effects.removeIndex(effects.indexOf(effect, false));
	}

	@Override
	public void reset() {
		effects = null;
	}
	
	
	
}
