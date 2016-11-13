package com.fullspectrum.fsm.transition;

public class CollisionTransitionData implements TransitionData{

	public CollisionType type;
	public boolean onCollide;
	
	public CollisionTransitionData(CollisionType type, boolean onCollide) {
		this.type = type;
		this.onCollide = onCollide;
	}
	
	@Override
	public void reset() {
		
	}
	
	public enum CollisionType{
		GROUND,
		CEILING,
		LEFT_WALL,
		RIGHT_WALL,
		LADDER
	}
}
