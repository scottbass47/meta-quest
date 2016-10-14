package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class SpeedComponent implements Component {

	public float speed;
	
	public SpeedComponent(float speed){
		this.speed = speed;
	}
	
}
