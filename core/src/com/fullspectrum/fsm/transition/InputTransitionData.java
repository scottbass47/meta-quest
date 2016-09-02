package com.fullspectrum.fsm.transition;

import com.badlogic.gdx.utils.Array;
import com.fullspectrum.input.Actions;

public class InputTransitionData implements ITransitionData{

	public Array<Actions> triggers;
	
	public InputTransitionData(){
		triggers = new Array<Actions>();
	}

	@Override
	public String toString() {
		String ret = "";
		for(Actions action : triggers){
			ret += action.name() + ", ";
		}
		return triggers.size > 0 ? ret.substring(0, ret.length() - 2) : "";
	}
	
	@Override
	public void reset() {
	}
	
}