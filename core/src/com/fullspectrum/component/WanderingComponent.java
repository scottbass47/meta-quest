package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class WanderingComponent implements Component{

	public int radius;
	public float idleTime;
	public boolean wandering = true;
	public float timeElapsed = 0.0f;
	
	public WanderingComponent(int radius, float idleTime) {
		this.radius = radius;
		this.idleTime = idleTime;
	}
	
}
