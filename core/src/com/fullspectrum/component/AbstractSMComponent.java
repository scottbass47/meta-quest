package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

// CLEANUP Machines null check
public abstract class AbstractSMComponent<T extends StateMachine<? extends State, ? extends StateObject>> implements Component, Poolable{

	protected Array<T> machines;
	
	public AbstractSMComponent() {
		machines = new Array<T>();
	}
	
	public AbstractSMComponent<T> set(T machine){
		machines.add(machine);
		return this;
	}
	
	public void add(T machine){
		if(machines == null) return;
		machines.add(machine);
	}
	
	public T first(){
		if(machines == null) return null;
		return machines.first();
	}
	
	public T get(int index){
		if(machines == null) return null;
		return machines.get(index);
	}

	public void remove(T machine){
		if(machines == null) return;
		machines.removeValue(machine, false);
	}
	
	public void remove(int index){
		if(machines == null) return;
		machines.removeIndex(index);
	}
	
	public int size(){
		if(machines == null) return 0;
		return machines.size;
	}
	
	public Array<T> getMachines(){
		return machines;
	}
	
	@Override
	public void reset() {
		for(T machine : machines){
			if(machine != null) machine.reset();
		}
		machines = null;
	}

}
