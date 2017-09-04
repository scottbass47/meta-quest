package com.cpubrew.fsm.transition;

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
	
	@Override
	public String toString() {
		return "Type: " + type.name() + ", onCollide: " + onCollide;
	}
	
	public enum CollisionType{
		GROUND,
		CEILING,
		LEFT_WALL,
		RIGHT_WALL,
		LADDER
	}
}
