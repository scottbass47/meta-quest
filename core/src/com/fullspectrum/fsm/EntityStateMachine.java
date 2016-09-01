package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.fullspectrum.component.IAnimState;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;

public class EntityStateMachine {

	private ArrayMap<IStateIdentifier, EntityState> states;
	private EntityState currentState;
	private Entity entity;
	
	public float animationTime;
	private IAnimState currentAnimation;
	
	public EntityStateMachine(Entity entity){
		this.entity = entity;
		this.states = new ArrayMap<IStateIdentifier, EntityState>();
	}
	
	public EntityState createState(IStateIdentifier key){
		EntityState state = new EntityState();
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
	
	public EntityState getCurrentState(){
		return currentState;
	}
	
	public IAnimState getAnimation(){
		return currentAnimation;
	}
}