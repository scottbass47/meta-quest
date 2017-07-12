package com.fullspectrum.fsm.transition;

public class TimeTransitionData implements TransitionData{

	public float timePassed = 0.0f;
	public float time = 0.0f;
	
	public TimeTransitionData(float time) {
		this.time = time;
	}
	
	@Override
	public void reset() {
		System.out.println("Time set to 0");
		timePassed = 0.0f;
	}

	@Override
	public String toString() {
		return "Time: " + time + "s";
	}
}
