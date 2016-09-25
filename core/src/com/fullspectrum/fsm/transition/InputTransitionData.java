package com.fullspectrum.fsm.transition;

import com.badlogic.gdx.utils.Array;

public class InputTransitionData implements TransitionData{

	public Array<InputTrigger> triggers;
	public Type type = Type.ALL;
	public boolean pressed = true;
	
	public InputTransitionData(Type type, boolean pressed){
		this.type = type;
		this.pressed = pressed;
		triggers = new Array<InputTrigger>();
	}

	@Override
	public String toString() {
		String ret = "";
		for(InputTrigger trigger : triggers){
			ret += trigger;
		}
		ret = triggers.size > 0 ? ret.substring(0, ret.length() - 2) : "";
		return ret + ", all: " + type.name() + ", pressed: " + pressed;
	}
	
	@Override
	public void reset() {
	}
	
	public enum Type{
		ONLY_ONE,
		ANY_ONE,
		ALL
	}
	
}
