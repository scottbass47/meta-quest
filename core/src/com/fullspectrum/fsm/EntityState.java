package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.IAnimState;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;

public class EntityState {

	private Array<Component> components;
	private ArrayMap<Transition, IStateIdentifier> transitionMap;
	private ArrayMap<Transition, Object> transitionData;
	private Array<Transition> transitions;
	private Array<TransitionTag> tags;
	protected IAnimState animState;
	
	protected EntityState(){
		components = new Array<Component>();
		transitionMap = new ArrayMap<Transition, IStateIdentifier>();
		transitionData = new ArrayMap<Transition, Object>();
		transitions = new Array<Transition>();
		tags = new Array<TransitionTag>();
	}
	
	public EntityState add(Component c){
		components.add(c);
		return this;
	}
	
	public EntityState addTag(TransitionTag tag){
		tags.add(tag);
		return this;
	}
	
	protected void addTransition(Transition transition, Object data, IStateIdentifier toState){
		transitionMap.put(transition, toState);
		transitionData.put(transition, data);
		transitions.add(transition);
	}
	
	public EntityState withAnimation(IAnimState anim){
		this.animState = anim;
		return this;
	}
	
	public Array<Component> getComponents(){
		return components;
	}
	
	public Array<TransitionTag> getTags(){
		return tags;
	}
	
	public ArrayMap<Transition, IStateIdentifier> getTransitionMap(){
		return transitionMap;
	}
	
	public ArrayMap<Transition, Object> getTransitionData(){
		return transitionData;
	}
	
	public Array<Transition> getTransitions(){
		return transitions;
	}
	
	public Object getTransitionData(Transition transition){
		return transitionData.get(transition);
	}
	
	public IStateIdentifier getState(Transition transition){
		return transitionMap.get(transition);
	}
	
}
