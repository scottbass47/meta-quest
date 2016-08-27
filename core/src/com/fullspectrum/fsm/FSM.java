package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;

public class FSM {

	private ArrayMap<IStateIdentifier, EntityState> states;
	private EntityState currentState;
	private Entity entity;
	
	public FSM(Entity entity){
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
		for(Component c : currentState.components){
			entity.remove(c.getClass());
		}
		for(Component c : newState.components){
			entity.add(c);
		}
		currentState = newState;
	}
}