package com.fullspectrum.fsm.transition;

import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class NullTransition extends TransitionSystem	{

	private static NullTransition instance;
	
	private NullTransition() {}
	
	public static NullTransition getInstance(){
		if(instance == null){
			instance = new NullTransition();
		}
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(StateMachine<? extends State, ? extends StateObject> machine : machines){
			TransitionObject obj = machine.getCurrentState().getFirstData(Transition.NULL);
			if(obj.data == null){
//				System.out.println(machine + "-> Falling");
				machine.changeState(machine.getCurrentState().getState(obj));
			}
		}
	}
	
}
