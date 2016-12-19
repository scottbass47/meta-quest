package com.fullspectrum.fsm.transition;

import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class TimeTransition extends TransitionSystem {

	private static TimeTransition instance;

	public static TimeTransition getInstance() {
		if (instance == null) {
			instance = new TimeTransition();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			TransitionObject obj = machine.getCurrentState().getFirstData(Transition.TIME);
			TimeTransitionData ttd = (TimeTransitionData) obj.data;
			if (ttd == null) {
				return;
			}
			ttd.timePassed += deltaTime;
			if (ttd.timePassed > ttd.time) {
				if(machine.changeState(obj)) break;
			}
		}
	}
}
