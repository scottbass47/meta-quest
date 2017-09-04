package com.cpubrew.fsm;

import com.badlogic.gdx.utils.Array;

public class TransitionTable {

	private StateMachine<? extends State, ? extends StateObject> machine;
	private Array<AbstractDef> transitions;
	
	public TransitionTable(StateMachine<? extends State, ? extends StateObject> machine){
		this.machine = machine;
		transitions = new Array<AbstractDef>();
	}
	
	public void addTransition(AbstractDef def){
		transitions.add(def);
		for(State state : machine.states.keys()){
			StateObject stateObj = machine.getState(state);
			if(def.matches(stateObj)){
				addTransitionTo(stateObj, def);
			}
		}
	}

	public void addState(State state) {
		StateObject stateObj = machine.getState(state);
		for(AbstractDef def : transitions){
			if(def.matches(stateObj)){
				addTransitionTo(stateObj, def);
			}
		}
	}
	
	private void addTransitionTo(StateObject stateObj, AbstractDef def){
		if(def instanceof TransitionDef){
			TransitionDef transitionDef = (TransitionDef) def;
			stateObj.addTransition(transitionDef.getTransition(), transitionDef.getData(), def.toState);
		}else if(def instanceof MultiTransitionDef){
			MultiTransitionDef multiDef = (MultiTransitionDef) def;
			stateObj.addMultiTransition(multiDef.getMultiTransition(), def.toState);
		}else{
			throw new RuntimeException("Can't add this type of AbstractDef to state.");
		}
	}
}
