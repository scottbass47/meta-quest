package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
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
	private Array<Component> components;
	protected Entity entity;
	protected StateMachine<? extends State, ? extends StateObject> machine;
	private boolean enabled = true;

	// Bits
	protected Bits bits;
	protected int bitOffset;
	
	// Debug
	protected String identifier;

	protected StateObject(Entity entity, StateMachine<? extends State, ? extends StateObject> machine) {
		setEntity(entity);
		setMachine(machine);
		transitionMap = new ArrayMap<TransitionObject, State>();
		transitions = new Array<Transition>();
		tags = new Array<TransitionTag>();
		bits = new Bits();
		components = new Array<Component>();
	}
	
	public void setEntity(Entity entity){
		this.entity = entity;
	}
	
	public void setMachine(StateMachine<? extends State, ? extends StateObject> machine){
		this.machine = machine;
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
			if (entry.key.transition.equals(transition) && machine.getState(entry.value).isEnabled()) {
				ret.add(entry.key);
			}
		}
		return ret;
	}

	public TransitionObject getFirstData(Transition transition) {
		Iterator<Entry<TransitionObject, State>> iter = transitionMap.iterator();
		while (iter.hasNext()) {
			Entry<TransitionObject, State> entry = iter.next();
			if (entry.key.transition.equals(transition) && machine.getState(entry.value).isEnabled()) {
				return entry.key;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> clazz){
		for(Component c : components){
			if(c.getClass() == clazz) return (T) c;
		}
		return null;
	}
	
	public StateObject add(Component c){
		components.add(c);
		return this;
	}
	
	public Array<Component> getComponents(){
		return components;
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
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void disable(){
		enabled = false;
	}
	
	public void enable(){
		enabled = true;
	}
	
	@Override
	public String toString() {
		return identifier;
	}
}
