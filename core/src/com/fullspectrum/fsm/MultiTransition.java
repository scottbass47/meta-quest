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

	protected ObjectSet<TransitionObject> transitionObjects;
	protected ObjectSet<Transition> transitions;
	protected ArrayMap<TransitionObject, Boolean> map;
	
	protected ObjectSet<MultiTransition> multiTransitions;
	
	private Array<OperationType> operationTypes;
	private Array<Object> operationObjects;
	
	public MultiTransition(MultiTransition multiTransition){
		init();
		addTransition(multiTransition);
	}
	
	public MultiTransition(Transition transition, TransitionData data){
		init();
		addTransition(transition, data);
	}
	
	public MultiTransition(Transition transition){
		this(transition, null);
	}
	
	private void init(){
		transitionObjects = new ObjectSet<TransitionObject>();
		transitions = new ObjectSet<Transition>();
		map = new ArrayMap<TransitionObject, Boolean>();
		
		multiTransitions = new ObjectSet<MultiTransition>();
		operationTypes = new Array<OperationType>();
		operationObjects = new Array<Object>();
	}
	
	private MultiTransition addTransition(Transition transition, TransitionData data){
//		if(transitions.contains(transition)) return null;
		TransitionObject obj = new TransitionObject(transition, data);
		transitionObjects.add(obj);
		transitions.add(transition);
		map.put(obj, false);
		operationObjects.add(obj);
		return this;
	}
	
	private MultiTransition addTransition(MultiTransition multiTransition){
		transitions.addAll(multiTransition.transitions);
		multiTransitions.add(multiTransition);
		operationObjects.add(multiTransition);
		return this;
	}
	
	public MultiTransition and(Transition transition, TransitionData data){
		addTransition(transition, data);
		operationTypes.add(OperationType.AND);
		return this;
	}
	
	public MultiTransition and(Transition transition){
		return and(transition, null);
	}
	
	public MultiTransition and(MultiTransition multiTransition){
		addTransition(multiTransition);
		operationTypes.add(OperationType.OR);
		return this;
	}
	
	public MultiTransition or(Transition transition, TransitionData data){
		addTransition(transition, data);
		operationTypes.add(OperationType.OR);
		return this;
	}
	
	public MultiTransition or(Transition transition){
		return and(transition, null);
	}
	
	public MultiTransition or(MultiTransition multiTransition){
		addTransition(multiTransition);
		operationTypes.add(OperationType.OR);
		return this;
	}
	
	public ObjectSet<TransitionObject> getTransitionObjects(Transition transition){
		ObjectSet<TransitionObject> ret = new ObjectSet<TransitionObject>();
		for(TransitionObject obj : transitionObjects){
			if(obj.transition == transition) ret.add(obj);;
		}
		for(MultiTransition multi : multiTransitions){
			ret.addAll(multi.getTransitionObjects(transition));
		}
		return ret;
	}
	
	public ObjectSet<TransitionObject> getAllTransitionObjects(){
		ObjectSet<TransitionObject> ret = new ObjectSet<TransitionObject>();
		for(TransitionObject obj : transitionObjects){
			ret.add(obj);
		}
		for(MultiTransition multi : multiTransitions){
			ret.addAll(multi.getAllTransitionObjects());
		}
		return ret;
	}

	/**
	 * Returns whether or not all transition requirements have been met.
	 * @return
	 */
	protected boolean shouldTransition(){
//		for(Iterator<Entry<TransitionObject, Boolean>> iter = map.iterator(); iter.hasNext();){
//			Entry<TransitionObject, Boolean> entry = iter.next();
//			if(!entry.value) return false;
//		}
//		for(MultiTransition multi : multiTransitions){
//			if(!multi.shouldTransition()) return false;
//		}
		
		boolean shouldTransition = false;
		Object first = operationObjects.get(operationObjects.size - 1);
		if(first instanceof TransitionObject){
			TransitionObject obj = (TransitionObject) first;
			shouldTransition = map.get(obj);
		}else{
			MultiTransition multi = (MultiTransition) first;
			shouldTransition = multi.shouldTransition();
		}
		for(int i = operationTypes.size - 1; i >= 0; i--){
			boolean next = false;
			OperationType type = operationTypes.get(i);
			Object object = operationObjects.get(i);
			if(object instanceof TransitionObject){
				TransitionObject obj = (TransitionObject) object;
				next = map.get(obj);
			}else{
				MultiTransition multi = (MultiTransition) object;
				next = multi.shouldTransition();
			}
			if(type == OperationType.AND){
				shouldTransition = shouldTransition && next;
			}else if(type == OperationType.OR){
				shouldTransition = shouldTransition || next;
			}
		}
		
		return shouldTransition;
	}
	
	/**
	 * Resets all transitions to false
	 */
	protected void resetMap(){
		for(Iterator<Entry<TransitionObject, Boolean>> iter = map.iterator(); iter.hasNext();){
			Entry<TransitionObject, Boolean> entry = iter.next();
			set(entry.key, false);
		}
		for(MultiTransition multi : multiTransitions){
			multi.resetMap();
		}
	}
	
	protected boolean contains(TransitionObject transitionObject){
		if(transitionObjects.contains(transitionObject)) return true;
		for(MultiTransition multi : multiTransitions){
			if(multi.contains(transitionObject)) return true;
		}
		return false;
	}
	
	protected void set(TransitionObject transitionObject, boolean value){
		if(map.containsKey(transitionObject)) map.put(transitionObject, value);
		for(MultiTransition multi : multiTransitions){
			multi.set(transitionObject, value);
		}
	}
	
	public boolean isEmpty(){
		return transitions.size == 0;
	}
	
	@Override
	// CLEANUP Improve multi transition toString (use || and && with parentheses to show hierarchy)
	public String toString() {
		return transitionObjects.toString();
	}
	
	public enum OperationType{
		AND,
		OR
	}
}