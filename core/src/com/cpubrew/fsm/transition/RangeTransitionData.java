package com.cpubrew.fsm.transition;

public class RangeTransitionData implements TransitionData{

	public float distance;
	public boolean inRange = true;
	public float fov = 360;
	
	@Override
	public String toString() {
		return "Range: " + distance + ", In Range: " + inRange;
	}

	@Override
	public void reset() {
	}
	
}
