package com.fullspectrum.fsm.transition;

import com.fullspectrum.input.Actions;

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
	
}
