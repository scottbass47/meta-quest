package com.fullspectrum.fsm.transition;

public class RandomTransitionData implements ITransitionData{

	public float waitTime = 0.0f;
	public float probability = 0.0f;
	public float timePassed = 0.0f;
	
	@Override
	public void reset() {
		timePassed = 0.0f;
	}
	
}
