package com.fullspectrum.ai;

import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;

public class AIController implements Input{
	
	private ArrayMap<Actions, Boolean> pressed;
	private ArrayMap<Actions, Boolean> justPressed;
	private ArrayMap<Actions, Float> actionValues;
	
	public AIController(){
		pressed = new ArrayMap<Actions, Boolean>();
		justPressed = new ArrayMap<Actions, Boolean>();
		actionValues = new ArrayMap<Actions, Float>();
		releaseAll();
	}
	
	public void press(Actions action){
		press(action, 1.0f);
	}
	
	public void justPress(Actions action){
		justPress(action, 1.0f);
	}
	
	public void press(Actions action, float amount){
		pressed.put(action, true);
		actionValues.put(action, amount);
	}
	
	public void justPress(Actions action, float amount){
		justPressed.put(action, true);
		actionValues.put(action, amount);
	}
	
	public void release(Actions action){
		pressed.put(action, false);
		justPressed.put(action, false);
		actionValues.put(action, 0.0f);
	}
	
	public void releaseAll(){
		for(Actions action : Actions.values()){
			pressed.put(action, false);
			justPressed.put(action, false);
			actionValues.put(action, 0.0f);
		}
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
		return actionValues.get(action);
	}

}
