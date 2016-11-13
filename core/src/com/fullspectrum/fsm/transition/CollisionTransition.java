package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class CollisionTransition extends TransitionSystem{

	private static CollisionTransition instance;
	
	private CollisionTransition() {}
	
	public static CollisionTransition getInstance(){
		if(instance == null){
			instance = new CollisionTransition();
		}
		return instance;
	}
	
	@Override
	public void update(float deltaTime) {
		for(StateMachine<? extends State, ? extends StateObject> machine : machines){
			Entity entity = machine.getEntity();
			CollisionComponent collisionComp = Mappers.collision.get(entity);
			for(TransitionObject obj : machine.getCurrentState().getData(Transition.COLLISION)){
				CollisionTransitionData data = (CollisionTransitionData)obj.data;
				boolean shouldTransition = false;
				switch(data.type){
				case CEILING:
					if(collisionComp.hittingCeiling()) shouldTransition = true;
					break;
				case GROUND:
					if(collisionComp.onGround()) shouldTransition = true;
					break;
				case LADDER:
					if(collisionComp.onLadder()) shouldTransition = true;
					break;
				case LEFT_WALL:
					if(collisionComp.onLeftWall()) shouldTransition = true;
					break;
				case RIGHT_WALL:
					if(collisionComp.onRightWall()) shouldTransition = true;
					break;
				default:
					break;
				}
				if((data.onCollide && shouldTransition) || (!data.onCollide && !shouldTransition)){
					machine.changeState(obj);
					break;
				}
			}
		}
	}
	
}
