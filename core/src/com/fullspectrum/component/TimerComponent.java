package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TimerComponent implements Component, Poolable{

	public float time = 0.0f;
	public boolean looping = false;
	public float elapsed = 0.0f;
	public TimeListener listener;
	
	public TimerComponent set(float time, boolean looping, TimeListener listener){
		this.time = time;
		this.looping = looping;
		this.listener = listener;
		return this;
	}
	
	@Override
	public void reset() {
		time = 0.0f;
		looping = false;
		elapsed = 0.0f;
		listener = null;
	}
	
}
