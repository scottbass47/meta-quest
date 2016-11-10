package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;

public class RangeTransitionData implements TransitionData{

	public Entity target;
	public float distance;
	public boolean inRange = true;
	public boolean rayTrace = true;
	
	@Override
	public String toString() {
		return "Range: " + distance + ", In Range: " + inRange + ", Ray Trace: " + rayTrace;
	}

	@Override
	public void reset() {
	}
	
}
