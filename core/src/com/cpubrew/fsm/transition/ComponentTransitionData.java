package com.cpubrew.fsm.transition;

import com.badlogic.ashley.core.Component;

public class ComponentTransitionData implements TransitionData{

	public Class<? extends Component> component;
	public boolean remove;
	
	public <T extends Component> ComponentTransitionData(Class<T> component, boolean remove) {
		this.component = component;
		this.remove = remove;
	}
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public String toString() {
		return component.getSimpleName() + (remove ? " on remove" : " on add");
	}
}