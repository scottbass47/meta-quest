package com.cpubrew.fsm.transition;

import com.cpubrew.input.Actions;

public class InputTrigger {

	public boolean justPressed = false;
	public Actions action;
	
	public InputTrigger(Actions action){
		this(action, false);
	}
	
	public InputTrigger(Actions action, boolean justPressed){
		this.action = action;
		this.justPressed = justPressed;
	}
	
	@Override
	public String toString() {
		return "[" + action.name() + " + justPressed: " + justPressed + "]";
	}
}
