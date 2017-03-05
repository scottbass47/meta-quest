package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.effects.EffectType;

public class ImmuneComponent implements Component, Poolable{

	private ObjectSet<EffectType> immuneTo;
	
	public ImmuneComponent() {
		immuneTo = new ObjectSet<EffectType>();
	}
	
	public ImmuneComponent add(EffectType type){
		immuneTo.add(type);
		return this;
	}
	
	public ImmuneComponent remove(EffectType type){
		if(immuneTo.contains(type)){
			immuneTo.remove(type);
		}
		return this;
	}
	
	public boolean isImmuneTo(EffectType type){
		return immuneTo.contains(type);
	}
	
	@Override
	public void reset() {
		immuneTo = null;
	}

}
