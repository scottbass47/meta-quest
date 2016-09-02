package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.fullspectrum.component.IAnimState;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionTag;

public class EntityState {

	private Array<Component> components;
	private ArrayMap<TransitionObject, IStateIdentifier> transitionMap;
//	private ArrayMap<Transition, IStateIdentifier> transitionMap;
//	private ArrayMap<Transition, Object> transitionData;
	private Array<Transition> transitions;
	private Array<TransitionTag> tags;
	protected IAnimState animState;
	
	// Bits
	protected Bits bits;
	protected int bitOffset;
	
	protected EntityState(){
		components = new Array<Component>();
//		transitionMap = new ArrayMap<Transition, IStateIdentifier>();
		transitionMap = new ArrayMap<TransitionObject, IStateIdentifier>();
//		transitionData = new ArrayMap<Transition, Object>();
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
	
	protected void addTransition(Transition transition, Object data, IStateIdentifier toState){
		TransitionObject obj = new TransitionObject(transition, data);
		// Assert that the transition being added is unique if it doesn't allow multiple
		assert(!(transitionMap.containsKey(obj) && !transition.allowMultiple));
		transitionMap.put(obj, toState);
//		transitionMap.put(transition, toState);
//		transitionData.put(transition, data);
		transitions.add(transition);
	}
	
	public EntityState withAnimation(IAnimState anim){
		this.animState = anim;
		return this;
	}
	
	public Array<Component> getComponents(){
		return components;
	}
	
	public Array<TransitionTag> getTags(){
		return tags;
	}
	
//	public ArrayMap<Transition, IStateIdentifier> getTransitionMap(){
//		return transitionMap;
//	}
	
	public Array<TransitionObject> getData(Transition transition){
		Array<TransitionObject> ret = new Array<TransitionObject>();
		Iterator<Entry<TransitionObject, IStateIdentifier>> iter = transitionMap.iterator();
		while(iter.hasNext()){
			Entry<TransitionObject, IStateIdentifier> entry = iter.next();
			if(entry.key.transition.equals(transition)){
				ret.add(entry.key);
			}
		}
		return ret;
	}
	
	public TransitionObject getFirstData(Transition transition){
		Iterator<Entry<TransitionObject, IStateIdentifier>> iter = transitionMap.iterator();
		while(iter.hasNext()){
			Entry<TransitionObject, IStateIdentifier> entry = iter.next();
			if(entry.key.transition.equals(transition)){
				return entry.key;
			}
		}
		return null;
	}
	
	public IStateIdentifier getState(TransitionObject transition){
		return transitionMap.get(transition);
	}
	
	public ArrayMap<TransitionObject, IStateIdentifier> getTransitionMap(){
		return transitionMap;
	}
	
//	public ArrayMap<Transition, Object> getTransitionData(){
//		return transitionData;
//	}
	
	public Array<Transition> getTransitions(){
		return transitions;
	}
	
//	public Object getTransitionData(Transition transition){
//		return transitionData.get(transition);
//	}
	
//	public IStateIdentifier getState(Transition transition){
//		return transitionMap.get(transition);
//	}
	
	@Override
	public String toString(){
		return animState.toString();
	}
	
}