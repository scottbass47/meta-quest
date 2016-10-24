package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;

public class RangeTransitionData {

	public Entity target;
	public float distance;
	public boolean inRange = true;
	
	@Override
	public String toString() {
		return "Range: " + distance + ", In Range: " + inRange;
	}
	
}
