package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class SpeedComponent implements Component {

	public final float maxSpeed;
	public float multiplier = 1.0f;
	
	public SpeedComponent(float maxSpeed){
		this.maxSpeed = maxSpeed;
	}
	
}
