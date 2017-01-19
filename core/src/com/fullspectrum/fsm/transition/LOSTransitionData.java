package com.fullspectrum.fsm.transition;

public class LOSTransitionData implements TransitionData{

	public boolean inSight;
	
	public LOSTransitionData(boolean inSight){
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
