package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.EntityStateMachine;

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
		for(Entity e : entities){
			FSMComponent fsmComp = Mappers.fsm.get(e);
			AnimationComponent animComp = Mappers.animation.get(e);
			assert(fsmComp != null && animComp != null);
			EntityStateMachine fsm = fsmComp.fsm;
			TransitionObject obj = fsm.getCurrentState().getFirstData(Transition.ANIMATION_FINISHED);
			if(fsm.animationTime > animComp.animations.get(fsm.getAnimation()).getAnimationDuration()){
//				System.out.println("Animation Finished");
				fsm.changeState(fsm.getCurrentState().getState(obj));
			}
		}
	}
}
