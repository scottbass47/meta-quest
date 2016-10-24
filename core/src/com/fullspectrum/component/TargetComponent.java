package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class TargetComponent implements Component{

	public Entity target;
	
	public TargetComponent(Entity target){
		this.target = target;
	}
	
}
