package com.fullspectrum.fsm.transition;

public class RandomTransitionData implements TransitionData{

	public float waitTime = 0.0f;
	public float probability = 0.5f;
	public float timePassed = 0.0f;
	
	@Override
	public void reset() {
		timePassed = 0.0f;
	}
	
	@Override
	public String toString() {
		return "Wait time: " + waitTime + ", Probability: " + probability;
	}
}