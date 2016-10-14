package com.fullspectrum.ai;

import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;

public class AIController implements Input{
	
	private ArrayMap<Actions, Boolean> pressed;
	private ArrayMap<Actions, Boolean> justPressed;
	
	public AIController(){
		pressed = new ArrayMap<Actions, Boolean>();
		justPressed = new ArrayMap<Actions, Boolean>();
	}
	
	public void press(Actions action){
		pressed.put(action, true);
	}
	
	public void justPress(Actions action){
		justPressed.put(action, true);
	}
	
	public void release(Actions action){
		pressed.put(action, false);
		justPressed.put(action, false);
	}

	@Override
	public boolean isJustPressed(Actions action) {
		return justPressed.get(action);
	}

	@Override
	public boolean isPressed(Actions action) {
		return pressed.get(action);
	}

	@Override
	public Float getValue(Actions action) {
		return pressed.get(action) || justPressed.get(action) ? 1.0f : 0.0f;
	}

}
