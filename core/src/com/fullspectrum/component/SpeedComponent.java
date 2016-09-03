package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class SpeedComponent implements Component {

	public final float speed;
	
	public SpeedComponent(float speed){
		this.speed = speed;
	}
	
}
