package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.fsm.transition.Tag;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionData;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionTag;

public class StateMachine<S extends State, E extends StateObject> {

	// State
	protected ArrayMap<E, StateMachine<? extends State, ? extends StateObject>> substateMachines;
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
	private String debugName;

	public StateMachine(Entity entity, StateCreator<E> creator, Class<S> stateClazz, Class<E> stateObjectClazz) {
		this.entity = entity;
		this.creator = creator;
		this.stateClazz = stateClazz;
		this.stateObjectClazz = stateObjectClazz;
		substateMachines = new ArrayMap<E, StateMachine<? extends State, ? extends StateObject>>();
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
			for (Transition t : currentState.getTransitions()) {
				t.getSystem().removeStateMachine(this);
			}
			StateResetSystem.getInstance().removeStateMachine(this);
			StateMachine<? extends State, ? extends StateObject> machine = substateMachines.get(currentState);
			if (machine != null) {
				machine.reset();
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
		substateMachines.put((E) state, machine);
	}

	private void exitCurrent() {
		if (currentState != null) {
			currentState.onExit();
			for (Transition t : currentState.getTransitions()) {
				t.getSystem().removeStateMachine(this);
			}
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
			StateResetSystem.getInstance().removeStateMachine(this);
			StateMachine<? extends State, ? extends StateObject> machine = substateMachines.get(currentState);
			if (machine != null) {
				machine.exitCurrent();
			}
		}
		currentState = null;
	}

	public void changeState(State identifier) {
		if (!stateClazz.isInstance(identifier)) throw new IllegalArgumentException("Incorrect state type.");
		@SuppressWarnings("unchecked")
		E newState = states.get((S) identifier);
		if (newState == null) throw new IllegalArgumentException("No state attached to identifier: " + identifier);
		if (newState == currentState) return;
		exitCurrent();
		for (Transition t : newState.getTransitions()) {
			t.getSystem().addStateMachine(this);
		}
		for (Component c : newState.getComponents()) {
			entity.add(c);
		}
		newState.onEnter();
		StateResetSystem.getInstance().addStateMachine(this);
		StateMachine<? extends State, ? extends StateObject> machine = substateMachines.get(newState);
		if (machine != null) {
			machine.changeState(machine.initialState);
		}
		currentState = newState;
	}

	public void changeState(TransitionObject obj) {
		if (currentState.getState(obj) != null) {
			changeState(currentState.getState(obj));
		}
		Array<MultiTransition> multiTransitions = currentState.getMultiTransitions();
		for (MultiTransition multi : multiTransitions) {
			if (multi.transitionObjects.contains(obj, false)) {
				multi.set(obj, true);
				if (multi.shouldTransition()) {
					multi.resetMap();
					changeState(currentState.getState(multi));
					return;
				}
				break;
			}
		}
	}

	protected void resetMultiTransitions() {
		Array<MultiTransition> multiTransitions = currentState.getMultiTransitions();
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
		if (multiTransition.transitionObjects.size == 0) throw new IllegalArgumentException("MultiTransition must have at least 1 transition!");
		states.get(fromState).addMultiTransition(multiTransition, toState);
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

	public String printTransitions() {
		String ret = "";
		Iterator<Entry<S, E>> iter = states.iterator();
		while (iter.hasNext()) {
			Entry<S, E> entry = iter.next();
			Iterator<Entry<TransitionObject, State>> iterator = entry.value.getTransitionMap().iterator();
			while (iterator.hasNext()) {
				Entry<TransitionObject, State> transition = iterator.next();
				String data = transition.key.data == null ? "" : "(" + transition.key.data.toString() + ")";
				ret += entry.key.toString() + ": " + transition.key.transition.toString() + data + " -> " + transition.value.toString() + "\n";
			}
			ret += "\n";
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
				}
				else {
					bits.set(tag.getIndex() + bitOffset);
				}
			}
			return bits;
		}
	}
}
