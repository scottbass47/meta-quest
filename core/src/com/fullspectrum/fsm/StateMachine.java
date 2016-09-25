package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.fullspectrum.fsm.transition.Tag;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;

public class StateMachine<S extends State, E extends StateObject> {

	// State
	protected ArrayMap<S, E> states;
	protected E currentState;
	protected Entity entity;
	private StateCreator<E> creator;

	// Bits
	private Builder builder = new Builder();
	private int bitOffset;
	private boolean firstState = false;
	
	// Debug
	private String debugName;

	public StateMachine(Entity entity, StateCreator<E> creator) {
		this.entity = entity;
		this.creator = creator;
		states = new ArrayMap<S, E>();
		this.creator = creator;
	}
	
	public E createState(S key) {
		// State identifiers must also be taggable
		assert (key instanceof Tag);
		if (!firstState) {
			firstState = true;
			bitOffset = key.numStates();
		}
//		E state = null;
//		try {
//			state = stateClass.newInstance();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
		E state = creator.getInstance();
		state.setEntity(entity);
		state.identifier = key.toString();
		state.bitOffset = bitOffset;
		state.bits.set(((Tag) key).getIndex());
		states.put(key, state);
		return state;
	}

	public E getCurrentState() {
		return currentState;
	}

	public Entity getEntity(){
		return entity;
	}

	public void changeState(State identifier) {
		@SuppressWarnings("unchecked")
		E newState = states.get((S)identifier);
		if(newState == null) throw new IllegalArgumentException("No state attached to identifier: " + identifier);
		if (newState == currentState) return;
		if (currentState != null) {
			for (Transition t : currentState.getTransitions()) {
				t.getSystem().removeStateMachine(this);
			}
		}
		for (Transition t : newState.getTransitions()) {
			t.getSystem().addStateMachine(this);
		}
		currentState = newState;
	}

	public void addTransition(S fromState, Transition transition, S toState) {
		states.get(fromState).addTransition(transition, null, toState);
	}

	public void addTransition(S fromState, Transition transition, Object data, S toState) {
		states.get(fromState).addTransition(transition, data, toState);
	}

	public void addTransition(TransitionTag fromTag, Transition transition, S toState) {
		addTransition(fromTag, transition, null, toState);
	}

	public void addTransition(TransitionTag fromTag, Transition transition, Object data, S toState) {
		Iterator<Entry<S, E>> iter = states.iterator();
		while (iter.hasNext()) {
			Entry<S, E> entry = iter.next();
			if (entry.value.getTags().contains(fromTag, true)) {
				addTransition(entry.key, transition, data, toState);
			}
		}
	}

	public void addTransition(Builder builder, Transition transition, S toState) {
		addTransition(builder, transition, null, toState);
	}

	public void addTransition(Builder builder, Transition transition, Object data, S toState) {
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
	
	public void setDebugName(String debugName){
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

	// public String printTransitions() {
	// String ret = "";
	// Iterator<Entry<StateIdentifier, EntityState>> iter = states.iterator();
	// while (iter.hasNext()) {
	// Entry<StateIdentifier, EntityState> entry = iter.next();
	// Iterator<Entry<TransitionObject, State>> iterator =
	// entry.value.getTransitionMap().iterator();
	// if (entry.key.equals(PlayerStates.FALLING)) {
	// System.out.print("");
	// }
	// while (iterator.hasNext()) {
	// Entry<TransitionObject, State> transition = iterator.next();
	// String data = transition.key.data == null ? "" : "(" +
	// transition.key.data.toString() + ")";
	// ret += entry.key.toString() + ": " + transition.key.transition.toString()
	// + data + " -> " + transition.value.toString() + "\n";
	// }
	// ret += "\n";
	// }
	// return ret;
	// }

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
