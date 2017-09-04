package com.cpubrew.fsm.transition;

import com.badlogic.gdx.math.MathUtils;

public class RandomTransitionData implements TransitionData{

	public float lower = 0.0f;
	public float upper = 0.0f;
	public float waitTime = 0.0f;
	public float timePassed = 0.0f;
	
	public RandomTransitionData(float lower, float upper) {
		if(lower >= upper) throw new IllegalArgumentException("Lower bounds can't be greater than or equal to upper bounds.");
		this.lower = lower;
		this.upper = upper;
		setWaitTime();
	}
	
	@Override
	public void reset() {
		timePassed = 0.0f;
		setWaitTime();
	}
	
	private void setWaitTime(){
		waitTime = MathUtils.random(lower, upper);
	}
	
	@Override
	public String toString() {
		return "Between: " + lower + " and " + upper;
	}
}