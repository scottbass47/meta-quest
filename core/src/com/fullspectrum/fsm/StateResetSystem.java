package com.fullspectrum.fsm;

import com.fullspectrum.fsm.transition.TransitionSystem;

public class StateResetSystem extends TransitionSystem {

	private static StateResetSystem instance;

	public static StateResetSystem getInstance() {
		if (instance == null) {
			instance = new StateResetSystem();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			machine.resetMultiTransitions();
		}
	}

}
