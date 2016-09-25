package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class FallingTransition extends TransitionSystem	{

	private static FallingTransition instance;
	
	public static FallingTransition getInstance(){
		if(instance == null){
			instance = new FallingTransition();
		}
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(StateMachine<? extends State, ? extends StateObject> machine : machines){
			Entity e = machine.getEntity();
			BodyComponent bodyComp = Mappers.body.get(e);
			assert(bodyComp != null);
			TransitionObject obj = machine.getCurrentState().getFirstData(Transition.FALLING);
			if(bodyComp.body.getLinearVelocity().y < 0){
				System.out.println(machine + "-> Falling");
				machine.changeState(machine.getCurrentState().getState(obj));
			}
		}
	}
	
}
