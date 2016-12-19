package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.StaminaComponent;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class StaminaTransition extends TransitionSystem{

private static StaminaTransition instance;
	
	public static StaminaTransition getInstance(){
		if(instance == null){
			instance = new StaminaTransition();
		}
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(StateMachine<? extends State, ? extends StateObject> machine : machines){
			Entity e = machine.getEntity();
			StaminaComponent staminaComp = Mappers.stamina.get(e);
			assert(staminaComp != null);
			for(TransitionObject obj : machine.getCurrentState().getData(Transition.STAMINA)){
				StaminaTransitionData staminaData = (StaminaTransitionData)obj.data;
				if(staminaComp.stamina >= staminaData.staminaNeeded){
					if(machine.changeState(obj)) break;
				}
			}
		}
	}
	
}
