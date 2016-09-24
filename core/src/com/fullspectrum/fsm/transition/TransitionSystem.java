package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class TransitionSystem extends EntitySystem{

	protected Array<StateMachine<? extends State, ? extends StateObject>> machines;
	
	protected TransitionSystem() { 
		machines = new Array<StateMachine<? extends State, ? extends StateObject>>(); 
	}
	
	public void addStateMachine(StateMachine<? extends State, ? extends StateObject> machine){
		machines.add(machine);
	}
	
	public void removeStateMachine(StateMachine<? extends State, ? extends StateObject> machine){
		machines.removeIndex(machines.indexOf(machine, true));
	}
	
}
