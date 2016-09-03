package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.fullspectrum.component.AnimState;
import com.fullspectrum.fsm.transition.Tag;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionTag;

public class EntityStateMachine {

	// State
	private ArrayMap<StateIdentifier, EntityState> states;
	private EntityState currentState;
	private Entity entity;
	
	// Animation
	public float animationTime;
	private AnimState currentAnimation;
	
	// Bits
	private Builder builder = new Builder();
	private int bitOffset;
	private boolean firstState = false;
	
	public EntityStateMachine(Entity entity){
		this.entity = entity;
		this.states = new ArrayMap<StateIdentifier, EntityState>();
	}
	
	public EntityState createState(StateIdentifier key){
		// State identifiers must also be taggable
		assert(key instanceof Tag);
		if(!firstState){
			firstState = true;
			bitOffset = key.numStates();
		}
		EntityState state = new EntityState();
		state.bitOffset = bitOffset;
		state.bits.set(((Tag)key).getIndex());
		states.put(key, state);
		return state;
	}
	
	public void changeState(StateIdentifier identifier){
		EntityState newState = states.get(identifier);
		if(newState == currentState) return;
		if(currentState != null){
			for(Transition t : currentState.getTransitions()){
				t.getSystem().removeEntity(entity);
			}
			for(Component c : currentState.getComponents()){
				entity.remove(c.getClass());
			}
		}
		for(Transition t : newState.getTransitions()){
			t.getSystem().addEntity(entity);
		}
		for(Component c : newState.getComponents()){
			entity.add(c);
		}
		animationTime = 0;
		currentAnimation = newState.animState;
		currentState = newState;
	}
	
	public void addTransition(StateIdentifier fromState, Transition transition, StateIdentifier toState){
		addTransition(fromState, transition, null, toState);
	}
	
	public void addTransition(StateIdentifier fromState, Transition transition, Object data, StateIdentifier toState){
		states.get(fromState).addTransition(transition, data, toState);
	}
	
	public void addTransition(TransitionTag fromTag, Transition transition, StateIdentifier toState){
		addTransition(fromTag, transition, null, toState);
	}
	
	public void addTransition(TransitionTag fromTag, Transition transition, Object data, StateIdentifier toState){
		Iterator<Entry<StateIdentifier, EntityState>> iter = states.iterator();
		while(iter.hasNext()){
			Entry<StateIdentifier, EntityState> entry = iter.next();
			if(entry.value.getTags().contains(fromTag, true)){
				addTransition(entry.key, transition, data, toState);
			}
		}
	}
	
	public void addTransition(Builder builder, Transition transition, StateIdentifier toState){
		addTransition(builder, transition, null, toState);
	}
	
	public void addTransition(Builder builder, Transition transition, Object data, StateIdentifier toState){
		Iterator<Entry<StateIdentifier, EntityState>> iter = states.iterator();
		while(iter.hasNext()){
			Entry<StateIdentifier, EntityState> entry = iter.next();
			EntityState state = entry.value;
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
	
	public Builder all(Tag... tags){
		return builder.reset().all(tags);
	}
	
	public Builder one(Tag... tags){
		return builder.reset().one(tags);
	}
	
	public Builder exclude(Tag... tags){
		return builder.reset().exclude(tags);
	}
	
	public EntityState getCurrentState(){
		return currentState;
	}
	
	public AnimState getAnimation(){
		return currentAnimation;
	}
	
	public String printTransitions(){
		String ret = "";
		Iterator<Entry<StateIdentifier, EntityState>> iter = states.iterator();
		while(iter.hasNext()){
			Entry<StateIdentifier, EntityState> entry = iter.next();
			Iterator<Entry<TransitionObject, StateIdentifier>> iterator = entry.value.getTransitionMap().iterator();
			if(entry.key.equals(PlayerStates.FALLING)){
				System.out.print("");
			}
			while(iterator.hasNext()){
				Entry<TransitionObject, StateIdentifier> transition = iterator.next();
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

		public final Builder one (Tag... tags) {
			one = getBits(tags);
			return this;
		}

		public final Builder exclude (Tag... tags) {
			exclude = getBits(tags);
			return this;
		}
		
		private Bits getBits(Tag... tags){
			Bits bits = new Bits();
			for(int i = 0; i < tags.length; i++){
				Tag tag = (Tag) tags[i];
				if(tag instanceof StateIdentifier){
					bits.set(tag.getIndex());
				}
				else{
					bits.set(tag.getIndex() + bitOffset);
				}
			}
			return bits;
		}
	}
}