package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.EntityStateMachine;

public class LandedTransition extends TransitionSystem {

	private static LandedTransition instance;

	public static LandedTransition getInstance() {
		if (instance == null) {
			instance = new LandedTransition();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		for (Entity e : entities) {
			FSMComponent fsmComp = Mappers.fsm.get(e);
			BodyComponent bodyComp = Mappers.body.get(e);
			assert(fsmComp != null && bodyComp != null);
			EntityStateMachine fsm = fsmComp.fsm;
			TransitionObject obj = fsm.getCurrentState().getFirstData(Transition.LANDED);
			if(bodyComp.body.getLinearVelocity().y == 0){
				System.out.println("Landed");
				fsm.changeState(fsm.getCurrentState().getState(obj));
			}
		}
	}

}
