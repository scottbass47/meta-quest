package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;

public class LOSTransitionData implements TransitionData{

	public Entity target;
	public boolean inSight;
	
	public LOSTransitionData(Entity target, boolean inSight){
		this.target = target;
		this.inSight = inSight;
	}
	
	@Override
	public String toString() {
		return (inSight ? "In Sight" : "Out of Sight");
	}
	
	@Override
	public void reset() {
		
	}

}
