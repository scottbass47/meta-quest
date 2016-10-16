package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class JumpComponent implements Component{

	public float maxForce;
	public float multiplier = 1.0f;
	
	public JumpComponent(float maxForce){
		this.maxForce = maxForce;
	}
	
}
