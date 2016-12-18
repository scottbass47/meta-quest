package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class AnimationFinishedTransition extends TransitionSystem{

	private static AnimationFinishedTransition instance;
	
	public static AnimationFinishedTransition getInstance(){
		if(instance == null){
			instance = new AnimationFinishedTransition();
		}
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(StateMachine<? extends State, ? extends StateObject> machine : machines){
			Entity e = machine.getEntity();
			AnimationComponent animComp = Mappers.animation.get(e);
			ESMComponent esmComp = Mappers.esm.get(e);
			assert(esmComp != null && animComp != null);
			EntityStateMachine esm = esmComp.esm;
			TransitionObject obj = machine.getCurrentState().getFirstData(Transition.ANIMATION_FINISHED);
			if(esm.getAnimationTime() >= animComp.animations.get(esm.getAnimation()).getAnimationDuration() - 0.1f){
//				System.out.println(machine + "-> Animation Finished");
				machine.changeState(obj);
			}
		}
	}
}
