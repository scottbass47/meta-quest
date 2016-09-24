package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.EntityStateMachine;

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
		for (Entity e : entities) {
			FSMComponent fsmComp = Mappers.fsm.get(e);
			assert (fsmComp != null);
			EntityStateMachine fsm = fsmComp.fsm;
			for (TransitionObject obj : fsm.getCurrentState().getData(Transition.RANDOM)) {
				RandomTransitionData rtd = (RandomTransitionData)obj.data;
				if (rtd == null) {
					rtd = new RandomTransitionData();
				}
				rtd.timePassed += deltaTime;
				if (rtd.timePassed < rtd.waitTime) continue;
				if (rtd.probability / deltaTime > Math.random()) {
					rtd.reset();
//					System.out.println("Random Transition");
					fsm.changeState(fsm.getCurrentState().getState(obj));
					break;
				}
			}
		}
	}

}
