package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.game.GdxGame;

public class RandomTransition extends TransitionSystem{

	private static RandomTransition instance;
	
	public static RandomTransition getInstance(){
		if(instance == null){
			instance = new RandomTransition();
		}
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(Entity e : entities){
			FSMComponent fsmComp = Mappers.fsm.get(e);
			assert(fsmComp != null);
			EntityStateMachine fsm = fsmComp.fsm;
			RandomTransitionData rtd = (RandomTransitionData)fsm.getCurrentState().getTransitionData(Transition.RANDOM);
			rtd.timePassed += deltaTime;
			if(rtd.timePassed < rtd.waitTime) continue;
			if(rtd.probability / GdxGame.UPS > Math.random()){
				rtd.reset();
				System.out.println("Random Transition");
				fsm.changeState(fsm.getCurrentState().getState(Transition.RANDOM));
			}
		}
	}
	
}
