package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class EntityState {

	protected Array<Component> components;
	
	protected EntityState(){
		components = new Array<Component>();
	}
	
	public EntityState add(Component c){
		components.add(c);
		return this;
	}
	
}
