package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CollisionComponent;
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
			CollisionComponent collisionComp = Mappers.collision.get(e);
			assert(bodyComp != null && collisionComp != null);
			TransitionObject obj = machine.getCurrentState().getFirstData(Transition.FALLING);
			if(bodyComp.body.getLinearVelocity().y < 0 && !collisionComp.onGround()){
//				System.out.println(machine + "-> Falling");
				machine.changeState(obj);
			}
		}
	}
	
}
