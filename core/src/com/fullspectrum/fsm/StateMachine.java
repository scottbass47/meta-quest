package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.fsm.transition.Tag;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionData;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionTag;

public class StateMachine<S extends State, E extends StateObject> {

	// State
	protected ArrayMap<E, Array<StateMachine<? extends State, ? extends StateObject>>> substateMachines;
	protected ArrayMap<S, E> states;
	protected E currentState;
	protected S initialState;
	protected Entity entity;
	private StateCreator<E> creator;
	private Class<S> stateClazz;
	private Class<E> stateObjectClazz;

	// Bits
	private Builder builder = new Builder();
	private int bitOffset;

	// Debug
	private String debugName = "";

	public StateMachine(Entity entity, StateCreator<E> creator, Class<S> stateClazz, Class<E> stateObjectClazz) {
		this.entity = entity;
		this.creator = creator;
		this.stateClazz = stateClazz;
		this.stateObjectClazz = stateObjectClazz;
		substateMachines = new ArrayMap<E, Array<StateMachine<? extends State, ? extends StateObject>>>();
		states = new ArrayMap<S, E>();
	}

	public E createState(S key) {
		// State identifiers must also be taggable
		assert (key instanceof Tag);
		if (initialState == null) {
			initialState = key;
			bitOffset = key.numStates();
		}
		E state = creator.getInstance(entity, this);
		state.identifier = key.toString();
		state.bitOffset = bitOffset;
		state.addTag(TransitionTag.ALL);
		state.bits.set(((Tag) key).getIndex());
		states.put(key, state);
		return state;
	}

	public void disableState(S state) {
		if (currentState == states.get(state)) return;
		states.get(state).disable();
	}

	public void enableState(S state) {
		states.get(state).enable();
	}

	public void reset() {
		if (currentState != null) {
			Iterator<Entry<S, E>> iter = states.iterator();
			while (iter.hasNext()) {
				Entry<S, E> state = iter.next();
				for (Component c : state.value.getComponents()) {
					Poolable pool = (Poolable) c;
					pool.reset();
				}
			}
			StateMachineSystem.getInstance().removeStateMachine(this);
			Array<StateMachine<? extends State, ? extends StateObject>> machines = substateMachines.get(currentState);
			if (machines != null) {
				for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
					machine.reset();
				}
			}
		}
		currentState = null;
	}

	public E getCurrentState() {
		return currentState;
	}

	@SuppressWarnings("unchecked")
	public E getState(State state) {
		if (!stateClazz.isInstance(state)) throw new IllegalArgumentException("Incorrect state type.");
		return states.get((S) state);
	}

	public Entity getEntity() {
		return entity;
	}

	@SuppressWarnings("unchecked")
	public void addSubstateMachine(StateObject state, StateMachine<? extends State, ? extends StateObject> machine) {
		if (!stateObjectClazz.isInstance(state)) throw new IllegalArgumentException("Incorrect state type.");
		Array<StateMachine<? extends State, ? extends StateObject>> machines = substateMachines.get((E) state);
		if (machines == null) {
			machines = new Array<StateMachine<? extends State, ? extends StateObject>>();
		}
		machines.add(machine);
		substateMachines.put((E) state, machines);
	}

	private void exitCurrent(S newState) {
		if (currentState != null) {
			currentState.onExit(newState);
			for (TransitionObject obj : currentState.getTranstionObjects()) {
				if (obj.data != null) {
					obj.data.reset();
				}
			}
			for (MultiTransition multi : currentState.getMultiTransitions()) {
				for (TransitionObject obj : multi.transitionObjects) {
					if (obj.data != null) {
						obj.data.reset();
					}
				}
			}
			for (Component c : currentState.getComponents()) {
				entity.remove(c.getClass());
			}
			StateMachineSystem.getInstance().removeStateMachine(this);
			Array<StateMachine<? extends State, ? extends StateObject>> machines = substateMachines.get(currentState);
			if (machines != null) {
				for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
					machine.exitCurrent(null);
				}
			}
		}
		currentState = null;
	}

	@SuppressWarnings("unchecked")
	public void changeState(State identifier) {
		if (!stateClazz.isInstance(identifier)) throw new IllegalArgumentException("Incorrect state type.");
		E newState = states.get((S) identifier);
		if (newState == null) throw new IllegalArgumentException("No state attached to identifier: " + identifier);
		if (newState == currentState) return;
		E currState = currentState;
		exitCurrent((S) identifier);
		for (Component c : newState.getComponents()) {
			entity.add(c);
		}
		if (currState != null) {
			newState.onEnter(states.getKey(currState, false));
		} else {
			newState.onEnter(null);
		}
		StateMachineSystem.getInstance().addStateMachine(this);
		Array<StateMachine<? extends State, ? extends StateObject>> machines = substateMachines.get(newState);
		if (machines != null) {
			for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
				machine.changeState(machine.initialState);
			}
		}
		currentState = newState;
	}

	public boolean changeState(TransitionObject obj) {
		if (currentState.getState(obj) != null) {
			changeState(currentState.getState(obj));
			return true;
		}
		ObjectSet<MultiTransition> multiTransitions = currentState.getMultiTransitions();
		for (MultiTransition multi : multiTransitions) {
			if (multi.contains(obj)) {
				multi.set(obj, true);
				if (multi.shouldTransition()) {
					multi.resetMap();
					changeState(currentState.getState(multi));
					return true;
				}
			}
		}
		return false;
	}

	protected void resetMultiTransitions() {
		ObjectSet<MultiTransition> multiTransitions = currentState.getMultiTransitions();
		for (MultiTransition multi : multiTransitions) {
			multi.resetMap();
		}
	}

	public void addTransition(S fromState, Transition transition, S toState) {
		states.get(fromState).addTransition(transition, null, toState);
	}

	public void addTransition(S fromState, Transition transition, TransitionData data, S toState) {
		states.get(fromState).addTransition(transition, data, toState);
	}

	public void addTransition(S fromState, MultiTransition multiTransition, S toState) {
		if (multiTransition.isEmpty()) throw new IllegalArgumentException("MultiTransition must have at least 1 transition!");
		states.get(fromState).addMultiTransition(multiTransition, toState);
	}

	public void addTransition(S fromState, Array<MultiTransition> multiTransitions, S toState) {
		for (MultiTransition multi : multiTransitions) {
			addTransition(fromState, multi, toState);
		}
	}

	public void addTransition(TransitionTag fromTag, Transition transition, S toState) {
		addTransition(fromTag, transition, null, toState);
	}

	public void addTransition(TransitionTag fromTag, Transition transition, TransitionData data, S toState) {
		Iterator<Entry<S, E>> iter = states.iterator();
		while (iter.hasNext()) {
			Entry<S, E> entry = iter.next();
			if (entry.value.bits.get(fromTag.getIndex() + bitOffset)) {
				addTransition(entry.key, transition, data, toState);
			}
		}
	}

	public void addTransition(TransitionTag fromTag, MultiTransition multiTransition, S toState) {
		if (multiTransition.transitionObjects.size == 0) throw new IllegalArgumentException("MultiTransition must have at least 1 transition!");
		for (Iterator<Entry<S, E>> iter = states.iterator(); iter.hasNext();) {
			Entry<S, E> entry = iter.next();
			if (entry.value.bits.get(fromTag.getIndex() + bitOffset)) {
				addTransition(entry.key, multiTransition, toState);
			}
		}
	}

	public void addTransition(TransitionTag fromTag, Array<MultiTransition> multiTransitions, S toState) {
		for (MultiTransition multi : multiTransitions) {
			addTransition(fromTag, multi, toState);
		}
	}

	public void addTransition(Builder builder, Transition transition, S toState) {
		addTransition(builder, transition, null, toState);
	}

	public void addTransition(Builder builder, Transition transition, TransitionData data, S toState) {
		Iterator<Entry<S, E>> iter = states.iterator();
		while (iter.hasNext()) {
			Entry<S, E> entry = iter.next();
			E state = entry.value;
			if (!state.bits.containsAll(builder.all)) {
				continue;
			}
			if (!builder.one.isEmpty() && !builder.one.intersects(state.bits)) {
				continue;
			}
			if (!builder.exclude.isEmpty() && builder.exclude.intersects(state.bits)) {
				continue;
			}
			addTransition(entry.key, transition, data, toState);
		}
	}

	public void addTransition(Builder builder, MultiTransition multiTransition, S toState) {
		for (Iterator<Entry<S, E>> iter = states.iterator(); iter.hasNext();) {
			Entry<S, E> entry = iter.next();
			E state = entry.value;
			if (!state.bits.containsAll(builder.all)) {
				continue;
			}
			if (!builder.one.isEmpty() && !builder.one.intersects(state.bits)) {
				continue;
			}
			if (!builder.exclude.isEmpty() && builder.exclude.intersects(state.bits)) {
				continue;
			}
			addTransition(entry.key, multiTransition, toState);
		}
	}

	public void addTransition(Builder builder, Array<MultiTransition> multiTransitions, S toState) {
		for (MultiTransition multi : multiTransitions) {
			addTransition(builder, multi, toState);
		}
	}

	public Array<State> getStates() {
		Array<State> ret = new Array<State>();
		Iterator<Entry<S, E>> iter = states.iterator();
		while (iter.hasNext()) {
			ret.add(iter.next().key);
		}
		return ret;
	}

	public void setDebugName(String debugName) {
		this.debugName = debugName;
	}
	
	public String getDebugName(){
		return debugName;
	}

	@Override
	public String toString() {
		return debugName != null ? debugName : "";
	}

	// ****************************************
	// * BUILDER *
	// ****************************************

	public Builder all(Tag... tags) {
		return builder.reset().all(tags);
	}

	public Builder one(Tag... tags) {
		return builder.reset().one(tags);
	}

	public Builder exclude(Tag... tags) {
		return builder.reset().exclude(tags);
	}
	
	public String printTransitions(){
		return printTransitions(false);
	}

	public String printTransitions(boolean reverse) {
		String ret = "";
		if(!debugName.isEmpty()){
			ret += debugName + " Transition Table\n";
			ret += "----------------------\n";
		}
		if (reverse) {
			for (S state : states.keys()) {
				for(Entry<S, E> e : states.entries()){
					if(e.key == state) continue;
					for (Entry<TransitionObject, State> transition : e.value.getTransitionMap().entries()) {
						if(!transition.value.equals(state)) continue;
						String data = transition.key.data == null ? "" : "(" + transition.key.data.toString() + ")";
						ret += state.getName() + " <- " + transition.key.transition.toString() + data + " <- " + e.key.getName() + "\n";
					}
					for (Entry<MultiTransition, State> transition : e.value.getMultiTransitionMap().entries()) {
						if(!transition.value.equals(state)) continue;
						ret += state.getName() + " <- " + transition.key + " <- " + e.key.getName() + "\n";
					}
				}
				ret += "\n";
			}
		} else {
			for (Iterator<Entry<S, E>> iter = states.iterator(); iter.hasNext();) {
				Entry<S, E> entry = iter.next();
				for (Iterator<Entry<TransitionObject, State>> iterator = entry.value.getTransitionMap().iterator(); iterator.hasNext();) {
					Entry<TransitionObject, State> transition = iterator.next();
					String data = transition.key.data == null ? "" : "(" + transition.key.data.toString() + ")";
					ret += entry.key.getName() + " -> " + transition.key.transition.toString() + data + " -> " + transition.value.getName() + "\n";
				}
				for (Iterator<Entry<MultiTransition, State>> iterator = entry.value.getMultiTransitionMap().iterator(); iterator.hasNext();) {
					Entry<MultiTransition, State> transition = iterator.next();
					ret += entry.key.getName() + " -> " + transition.key + " -> " + transition.value.getName() + "\n";
				}
				ret += "\n";
			}
		}
		return ret;
	}

	public class Builder {
		private Bits all;
		private Bits one;
		private Bits exclude;

		Builder() {
			all = new Bits();
			one = new Bits();
			exclude = new Bits();
		}

		public Builder reset() {
			all.clear();
			one.clear();
			exclude.clear();
			return this;
		}

		public final Builder all(Tag... tags) {
			all = getBits(tags);
			return this;
		}

		public final Builder one(Tag... tags) {
			one = getBits(tags);
			return this;
		}

		public final Builder exclude(Tag... tags) {
			exclude = getBits(tags);
			return this;
		}

		private Bits getBits(Tag... tags) {
			Bits bits = new Bits();
			for (int i = 0; i < tags.length; i++) {
				Tag tag = (Tag) tags[i];
				if (tag instanceof State) {
					bits.set(tag.getIndex());
				} else {
					bits.set(tag.getIndex() + bitOffset);
				}
			}
			return bits;
		}
	}
}
