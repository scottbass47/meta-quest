package com.cpubrew.fsm.transition;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.fsm.State;
import com.cpubrew.fsm.StateMachine;
import com.cpubrew.fsm.StateObject;

public class TransitionSystem extends EntitySystem{

	protected ObjectSet<StateMachine<? extends State, ? extends StateObject>> machines;
	
	protected TransitionSystem() { 
		machines = new ObjectSet<StateMachine<? extends State, ? extends StateObject>>(); 
	}
	
	public void addStateMachine(StateMachine<? extends State, ? extends StateObject> machine){
		machines.add(machine);
	}
	
	public void removeStateMachine(StateMachine<? extends State, ? extends StateObject> machine){
		machines.remove(machine);
	}
	
	public ObjectSet<StateMachine<? extends State, ? extends StateObject>> getMachines(){
		return machines;
	}
 
	
}
