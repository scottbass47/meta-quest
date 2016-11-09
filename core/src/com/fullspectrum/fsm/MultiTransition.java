package com.fullspectrum.fsm;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionObject;

public class MultiTransition {

	protected Array<TransitionObject> transitionObjects;
	protected ObjectSet<Transition> transitions;
	
	public MultiTransition(){
		transitionObjects = new Array<TransitionObject>();
		transitions = new ObjectSet<Transition>();
	}
	
	public MultiTransition addTransition(Transition transition, Object data){
		if(transitions.contains(transition)) return null;
		transitionObjects.add(new TransitionObject(transition, data));
		transitions.add(transition);
		return this;
	}
	
	public MultiTransition addTransition(Transition transition){
		return addTransition(transition, null);
	}
	
	public TransitionObject getTransitionObject(Transition transition){
		for(TransitionObject obj : transitionObjects){
			if(obj.transition == transition) return obj;
		}
		return null;
	}
}
