package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionData;
import com.fullspectrum.fsm.transition.TransitionObject;

public class MultiTransition {

	protected Array<TransitionObject> transitionObjects;
	protected ObjectSet<Transition> transitions;
	protected ArrayMap<TransitionObject, Boolean> map;
	
	public MultiTransition(){
		transitionObjects = new Array<TransitionObject>();
		transitions = new ObjectSet<Transition>();
		map = new ArrayMap<TransitionObject, Boolean>();
	}
	
	public MultiTransition addTransition(Transition transition, TransitionData data){
//		if(transitions.contains(transition)) return null;
		TransitionObject obj = new TransitionObject(transition, data);
		transitionObjects.add(obj);
		transitions.add(transition);
		map.put(obj, false);
		return this;
	}
	
	public MultiTransition addTransition(Transition transition){
		return addTransition(transition, null);
	}
	
	public Array<TransitionObject> getTransitionObject(Transition transition){
		Array<TransitionObject> ret = new Array<TransitionObject>();
		for(TransitionObject obj : transitionObjects){
			if(obj.transition == transition) ret.add(obj);;
		}
		return ret;
	}

	/**
	 * Returns whether or not all transition requirements have been met.
	 * @return
	 */
	protected boolean shouldTransition(){
		for(Iterator<Entry<TransitionObject, Boolean>> iter = map.iterator(); iter.hasNext();){
			Entry<TransitionObject, Boolean> entry = iter.next();
			if(!entry.value) return false;
		}
		return true;
	}
	
	/**
	 * Resets all transitions to false
	 */
	protected void resetMap(){
		for(Iterator<Entry<TransitionObject, Boolean>> iter = map.iterator(); iter.hasNext();){
			Entry<TransitionObject, Boolean> entry = iter.next();
			set(entry.key, false);
		}
	}
	
	protected void set(TransitionObject transitionObject, boolean value){
		map.put(transitionObject, value);
	}
	
	@Override
	public String toString() {
		return transitionObjects.toString();
	}
}
