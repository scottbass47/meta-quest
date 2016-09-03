package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.fullspectrum.component.AnimState;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionTag;

public class EntityState {

	// Data
	private Array<Component> components;
	private ArrayMap<TransitionObject, StateIdentifier> transitionMap;
	private Array<Transition> transitions;
	private Array<TransitionTag> tags;
	protected AnimState animState;
	
	// Bits
	protected Bits bits;
	protected int bitOffset;
	
	protected EntityState(){
		components = new Array<Component>();
		transitionMap = new ArrayMap<TransitionObject, StateIdentifier>();
		transitions = new Array<Transition>();
		tags = new Array<TransitionTag>();
		bits = new Bits();
	}
	
	public EntityState add(Component c){
		components.add(c);
		return this;
	}
	
	public EntityState addTag(TransitionTag tag){
		tags.add(tag);
		bits.set(tag.getIndex() + bitOffset);
		return this;
	}
	
	protected void addTransition(Transition transition, Object data, StateIdentifier toState){
		TransitionObject obj = new TransitionObject(transition, data);
		// Assert that the transition being added is unique if it doesn't allow multiple transitions of its type
		assert(!(transitionMap.containsKey(obj) && !transition.allowMultiple));
		transitionMap.put(obj, toState);
		transitions.add(transition);
	}
	
	public EntityState withAnimation(AnimState anim){
		this.animState = anim;
		return this;
	}
	
	public Array<Component> getComponents(){
		return components;
	}
	
	public Array<TransitionTag> getTags(){
		return tags;
	}
	
	public Array<TransitionObject> getData(Transition transition){
		Array<TransitionObject> ret = new Array<TransitionObject>();
		Iterator<Entry<TransitionObject, StateIdentifier>> iter = transitionMap.iterator();
		while(iter.hasNext()){
			Entry<TransitionObject, StateIdentifier> entry = iter.next();
			if(entry.key.transition.equals(transition)){
				ret.add(entry.key);
			}
		}
		return ret;
	}
	
	public TransitionObject getFirstData(Transition transition){
		Iterator<Entry<TransitionObject, StateIdentifier>> iter = transitionMap.iterator();
		while(iter.hasNext()){
			Entry<TransitionObject, StateIdentifier> entry = iter.next();
			if(entry.key.transition.equals(transition)){
				return entry.key;
			}
		}
		return null;
	}
	
	public StateIdentifier getState(TransitionObject transition){
		return transitionMap.get(transition);
	}
	
	public ArrayMap<TransitionObject, StateIdentifier> getTransitionMap(){
		return transitionMap;
	}
	
	public Array<Transition> getTransitions(){
		return transitions;
	}
	
	@Override
	public String toString(){
		return animState.toString();
	}
	
}
