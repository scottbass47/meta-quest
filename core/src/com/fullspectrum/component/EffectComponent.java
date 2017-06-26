package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.effects.Effect;
import com.fullspectrum.effects.EffectType;

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
	
	public boolean hasEffect(EffectType type) {
		for(Effect effect : effects) {
			if(effect.getType() == type) return true;
		}
		return false;
	}
	
	/** Returns the first effect of the specified effect type */
	public Effect getEffect(EffectType type){
		for(Effect effect : effects) {
			if(effect.getType() == type) return effect;
		}
		return null;
	}

	@Override
	public void reset() {
		effects = null;
	}
	
	
	
}
