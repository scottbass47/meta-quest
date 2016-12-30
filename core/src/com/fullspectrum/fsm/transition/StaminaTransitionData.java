package com.fullspectrum.fsm.transition;

public class StaminaTransitionData implements TransitionData{

	public float staminaNeeded;

	public StaminaTransitionData(float staminaNeeded) {
		this.staminaNeeded = staminaNeeded;
	}
	
	@Override
	public void reset() {
	}

	@Override
	public String toString() {
		return "" + staminaNeeded;
	}
	
}
