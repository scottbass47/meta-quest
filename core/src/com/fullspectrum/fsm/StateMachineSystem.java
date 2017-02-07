package com.fullspectrum.fsm;

import java.util.Iterator;

import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionSystem;

public class StateMachineSystem extends TransitionSystem {

	private static StateMachineSystem instance;
	private ObjectSet<StateMachine<? extends State, ? extends StateObject>> toRemove;
	private ObjectSet<StateMachine<? extends State, ? extends StateObject>> toAdd;

	private StateMachineSystem(){
		toRemove = new ObjectSet<StateMachine<? extends State,? extends StateObject>>();
		toAdd = new ObjectSet<StateMachine<? extends State,? extends StateObject>>();
	}
	
	public static StateMachineSystem getInstance() {
		if (instance == null) {
			instance = new StateMachineSystem();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			if(machine.getCurrentStateObject() == null) continue;
			for (TransitionObject obj : machine.getCurrentStateObject().getAllTransitionObjects()) {
				if (obj.transition.shouldTransition(machine.getEntity(), obj, deltaTime)) {
					if (machine.changeState(obj)) break;
				}
			}
			machine.resetMultiTransitions();
		}
		
		// Only add/remove after all machines have updated
		for(Iterator<StateMachine<? extends State, ? extends StateObject>> iter = toRemove.iterator(); iter.hasNext();){
			StateMachine<? extends State, ? extends StateObject> machine = iter.next();
			machines.remove(machine);
			iter.remove();
		}
		for(Iterator<StateMachine<? extends State, ? extends StateObject>> iter = toAdd.iterator(); iter.hasNext();){
			machines.add(iter.next());
			iter.remove();
		}
	}
	
	@Override
	public void addStateMachine(StateMachine<? extends State, ? extends StateObject> machine) {
		toAdd.add(machine);
	}
	
	@Override
	public void removeStateMachine(StateMachine<? extends State, ? extends StateObject> machine) {
		if(toAdd.contains(machine)){
			toAdd.remove(machine);
			return;
		}
		toRemove.add(machine);
	}
	
}
