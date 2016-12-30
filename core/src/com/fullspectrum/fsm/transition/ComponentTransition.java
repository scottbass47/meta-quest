package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class ComponentTransition extends TransitionSystem {

	private static ComponentTransition instance;

	public static ComponentTransition getInstance() {
		if (instance == null) {
			instance = new ComponentTransition();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			for (TransitionObject obj : machine.getCurrentState().getData(Transition.COMPONENT)) {
				ComponentTransitionData ctd = (ComponentTransitionData)obj.data;
				Component comp = machine.getEntity().getComponent(ctd.component);
				if ((comp == null && ctd.remove) || (comp != null && !ctd.remove)) {
					if(machine.changeState(obj)) break;
				}
			}
		}
	}

}
