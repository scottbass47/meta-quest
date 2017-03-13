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
	
	public ImmuneComponent addAll(ObjectSet<EffectType> types){
		immuneTo.addAll(types);
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
	
	public ObjectSet<EffectType> getImmunities(){
		return immuneTo;
	}
	
	public void setImmunies(ObjectSet<EffectType> immunities){
		this.immuneTo = immunities;
	}
	
	@Override
	public void reset() {
		immuneTo = null;
	}

}
