package com.fullspectrum.fsm.transition;

import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class InvalidEntityTransition extends TransitionSystem	{

	private static InvalidEntityTransition instance;
	
	private InvalidEntityTransition() {}
	
	public static InvalidEntityTransition getInstance(){
		if(instance == null){
			instance = new InvalidEntityTransition();
		}
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(StateMachine<? extends State, ? extends StateObject> machine : machines){
			TransitionObject obj = machine.getCurrentState().getFirstData(Transition.INVALID_ENTITY);
			if(obj.data == null || !EntityUtils.isValid(((InvalidEntityData)obj.data).toFollow)){
//				System.out.println(machine + "-> Falling");
				machine.changeState(obj);
			}
		}
	}
	
}
