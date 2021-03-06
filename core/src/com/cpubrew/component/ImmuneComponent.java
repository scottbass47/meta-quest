package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.effects.EffectType;

public class ImmuneComponent implements Component, Poolable {

	private ArrayMap<EffectType, Integer> iMap;
	private int immunityToAll = 0;
	
	public ImmuneComponent() {
		iMap = new ArrayMap<EffectType, Integer>();
	}
	
	public ImmuneComponent add(EffectType type){
		if(!iMap.containsKey(type)) iMap.put(type, 0);
		iMap.put(type, iMap.get(type) + 1);
		return this;
	}
	
	public ImmuneComponent remove(EffectType type){
		if(!iMap.containsKey(type) || iMap.get(type) <= 0) return this;
		iMap.put(type, iMap.get(type) - 1);
		return this;
	}
	
	public ImmuneComponent addAll(EffectType... types){
		for(EffectType type : types) add(type);
		return this;
	}
	
	public ImmuneComponent addAll(ObjectSet<EffectType> types){
		for(EffectType type : types) add(type);
		return this;
	}
	
	public ImmuneComponent removeAll(EffectType... types){
		for(EffectType type : types) remove(type);
		return this;
	}
	
	public ImmuneComponent removeAll(ObjectSet<EffectType> types){
		for(EffectType type : types) remove(type);
		return this;
	}
	
	public ImmuneComponent addCompleteImmunity() {
		immunityToAll++;
		return this;
	}
	
	public ImmuneComponent removeCompleteImmunity() {
		if(immunityToAll == 0) throw new RuntimeException("Can't remove immunity to all if entity has no immunity to all.");
		immunityToAll--;
		return this;
	}
	
	
	public void clear() {
		for(EffectType type : iMap.keys()) {
			iMap.put(type, 0);
		}
		immunityToAll = 0;
	}
	
	public boolean isImmuneTo(EffectType type){
		return immunityToAll > 0 || (iMap.get(type) != null && iMap.get(type) > 0);
	}
	
	@Override
	public void reset() {
		iMap = null;
		immunityToAll = 0;
	}
}
