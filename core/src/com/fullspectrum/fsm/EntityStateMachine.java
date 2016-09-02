package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.fullspectrum.component.IAnimState;
import com.fullspectrum.fsm.transition.ITag;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionTag;

public class EntityStateMachine {

	// State
	private ArrayMap<IStateIdentifier, EntityState> states;
	private EntityState currentState;
	private Entity entity;
	
	// Animation
	public float animationTime;
	private IAnimState currentAnimation;
	
	// Bits
	private Builder builder = new Builder();
	private int bitOffset;
	private boolean firstState = false;
	
	public EntityStateMachine(Entity entity){
		this.entity = entity;
		this.states = new ArrayMap<IStateIdentifier, EntityState>();
	}
	
	public EntityState createState(IStateIdentifier key){
		// State identifiers must also be taggable
		assert(key instanceof ITag);
		if(!firstState){
			firstState = true;
			bitOffset = key.numStates();
		}
		EntityState state = new EntityState();
		state.bitOffset = bitOffset;
		state.bits.set(((ITag)key).getIndex());
		states.put(key, state);
		return state;
	}
	
	public void changeState(IStateIdentifier identifier){
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
	
	public void addTransition(IStateIdentifier fromState, Transition transition, IStateIdentifier toState){
		addTransition(fromState, transition, null, toState);
	}
	
	public void addTransition(IStateIdentifier fromState, Transition transition, Object data, IStateIdentifier toState){
		states.get(fromState).addTransition(transition, data, toState);
	}
	
	public void addTransition(TransitionTag fromTag, Transition transition, IStateIdentifier toState){
		addTransition(fromTag, transition, null, toState);
	}
	
	public void addTransition(TransitionTag fromTag, Transition transition, Object data, IStateIdentifier toState){
		Iterator<Entry<IStateIdentifier, EntityState>> iter = states.iterator();
		while(iter.hasNext()){
			Entry<IStateIdentifier, EntityState> entry = iter.next();
			if(entry.value.getTags().contains(fromTag, true)){
				addTransition(entry.key, transition, data, toState);
			}
		}
	}
	
	public void addTransition(Builder builder, Transition transition, IStateIdentifier toState){
		addTransition(builder, transition, null, toState);
	}
	
	public void addTransition(Builder builder, Transition transition, Object data, IStateIdentifier toState){
		Iterator<Entry<IStateIdentifier, EntityState>> iter = states.iterator();
		while(iter.hasNext()){
			Entry<IStateIdentifier, EntityState> entry = iter.next();
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
	
	public Builder all(ITag... tags){
		return builder.reset().all(tags);
	}
	
	public Builder one(ITag... tags){
		return builder.reset().one(tags);
	}
	
	public Builder exclude(ITag... tags){
		return builder.reset().exclude(tags);
	}
	
	public EntityState getCurrentState(){
		return currentState;
	}
	
	public IAnimState getAnimation(){
		return currentAnimation;
	}
	
	public String printTransitions(){
		String ret = "";
		Iterator<Entry<IStateIdentifier, EntityState>> iter = states.iterator();
		while(iter.hasNext()){
			Entry<IStateIdentifier, EntityState> entry = iter.next();
			Iterator<Entry<TransitionObject, IStateIdentifier>> iterator = entry.value.getTransitionMap().iterator();
			if(entry.key.equals(PlayerStates.FALLING)){
				System.out.print("");
			}
			while(iterator.hasNext()){
				Entry<TransitionObject, IStateIdentifier> transition = iterator.next();
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

		public final Builder all(ITag... tags) {
			all = getBits(tags);
			return this;
		}

		public final Builder one (ITag... tags) {
			one = getBits(tags);
			return this;
		}

		public final Builder exclude (ITag... tags) {
			exclude = getBits(tags);
			return this;
		}
		
		private Bits getBits(ITag... tags){
			Bits bits = new Bits();
			for(int i = 0; i < tags.length; i++){
				ITag tag = (ITag) tags[i];
				if(tag instanceof IStateIdentifier){
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