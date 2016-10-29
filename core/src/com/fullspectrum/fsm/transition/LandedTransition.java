package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

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
		for(StateMachine<? extends State, ? extends StateObject> machine : machines){
			Entity e = machine.getEntity();
//			BodyComponent bodyComp = Mappers.body.get(e);
			CollisionComponent collisionComp = Mappers.collision.get(e);
			assert(collisionComp != null);
			TransitionObject obj = machine.getCurrentState().getFirstData(Transition.LANDED);
			if(collisionComp.onGround()){
//				System.out.println(machine + "-> Landed");
				machine.changeState(machine.getCurrentState().getState(obj));
			}
		}
	}

}
