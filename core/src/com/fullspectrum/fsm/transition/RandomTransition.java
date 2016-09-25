package com.fullspectrum.fsm.transition;

import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class RandomTransition extends TransitionSystem {

	private static RandomTransition instance;

	public static RandomTransition getInstance() {
		if (instance == null) {
			instance = new RandomTransition();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			for (TransitionObject obj : machine.getCurrentState().getData(Transition.RANDOM)) {
				RandomTransitionData rtd = (RandomTransitionData)obj.data;
				if (rtd == null) {
					rtd = new RandomTransitionData();
				}
				rtd.timePassed += deltaTime;
				if (rtd.timePassed < rtd.waitTime) continue;
				if (rtd.probability / deltaTime > Math.random()) {
					rtd.reset();
					System.out.println(machine + "-> Random Transition");
					machine.changeState(machine.getCurrentState().getState(obj));
					break;
				}
			}
		}
	}

}
