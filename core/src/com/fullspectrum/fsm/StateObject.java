package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionTag;

public class StateObject{
	// Data
	private ArrayMap<TransitionObject, State> transitionMap;
	private Array<Transition> transitions;
	private Array<TransitionTag> tags;
	protected Entity entity;

	// Bits
	protected Bits bits;
	protected int bitOffset;

	protected StateObject() {
		transitionMap = new ArrayMap<TransitionObject, State>();
		transitions = new Array<Transition>();
		tags = new Array<TransitionTag>();
		bits = new Bits();
	}
	
	public void setEntity(Entity entity){
		this.entity = entity;
	}

	public StateObject addTag(TransitionTag tag) {
		tags.add(tag);
		bits.set(tag.getIndex() + bitOffset);
		return this;
	}

	protected void addTransition(Transition transition, Object data, State toState) {
		TransitionObject obj = new TransitionObject(transition, data);
		// Assert that the transition being added is unique if it doesn't allow
		// multiple transitions of its type
		assert (!(transitionMap.containsKey(obj) && !transition.allowMultiple));
		transitionMap.put(obj, toState);
		transitions.add(transition);
	}

	public Array<TransitionTag> getTags() {
		return tags;
	}

	public Array<TransitionObject> getData(Transition transition) {
		Array<TransitionObject> ret = new Array<TransitionObject>();
		Iterator<Entry<TransitionObject, State>> iter = transitionMap.iterator();
		while (iter.hasNext()) {
			Entry<TransitionObject, State> entry = iter.next();
			if (entry.key.transition.equals(transition)) {
				ret.add(entry.key);
			}
		}
		return ret;
	}

	public TransitionObject getFirstData(Transition transition) {
		Iterator<Entry<TransitionObject, State>> iter = transitionMap.iterator();
		while (iter.hasNext()) {
			Entry<TransitionObject, State> entry = iter.next();
			if (entry.key.transition.equals(transition)) {
				return entry.key;
			}
		}
		return null;
	}

	public State getState(TransitionObject transition) {
		return transitionMap.get(transition);
	}

	public ArrayMap<TransitionObject, State> getTransitionMap() {
		return transitionMap;
	}

	public Array<Transition> getTransitions() {
		return transitions;
	}
}
