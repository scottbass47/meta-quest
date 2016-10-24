package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class RangeTransition extends TransitionSystem{

	private static RangeTransition instance;
	
	private RangeTransition(){}
	
	public static RangeTransition getInstance(){
		if(instance == null){
			instance = new RangeTransition();
		}
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(StateMachine<? extends State, ? extends StateObject> machine : machines){
			Entity entity = machine.getEntity();
			
			BodyComponent bodyComp = Mappers.body.get(entity);
			
			for (TransitionObject obj : machine.getCurrentState().getData(Transition.RANGE)) {
				RangeTransitionData rtd = (RangeTransitionData) obj.data;
				if (rtd == null || rtd.target == null) continue;
				BodyComponent otherBody = Mappers.body.get(rtd.target);
				
				Body b1 = bodyComp.body;
				Body b2 = otherBody.body;
				
				float x1 = b1.getPosition().x;
				float y1 = b1.getPosition().y;
				float x2 = b2.getPosition().x;
				float y2 = b2.getPosition().y;
				
				if(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 * y2) < rtd.distance * rtd.distance && rtd.inRange) ||
					((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 * y2) > rtd.distance * rtd.distance && !rtd.inRange)){
					machine.changeState(machine.getCurrentState().getState(obj));
					break;
				}
			}
		}
	}
}
