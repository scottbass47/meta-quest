package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class JumpComponent implements Component{

	public float force;
	
	public JumpComponent(float force){
		this.force = force;
	}
	
}
