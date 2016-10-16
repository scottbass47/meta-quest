package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class FollowComponent implements Component{

	public Entity toFollow;
	
	public FollowComponent(Entity toFollow){
		this.toFollow = toFollow;
	}
	
}
