package com.fullspectrum.fsm.transition;

import com.badlogic.gdx.utils.Array;
import com.fullspectrum.input.Actions;

// CLEANUP Input transitions should be singular and MultiTransitions should be used
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
			ret += trigger + ", ";
		}
		ret = triggers.size > 0 ? ret.substring(0, ret.length() - 2) : "";
		return ret + ", type: " + type.name() + ", " + (pressed ? "press" : "release");
	}
	
	@Override
	public void reset() {
	}
	
	public enum Type{
		ONLY_ONE,
		ANY_ONE,
		ALL
	}
	
	public static class Builder{
		private InputTransitionData data;
		
		public Builder(Type type, boolean pressed){
			data = new InputTransitionData(type, pressed);
		}
		
		public Builder add(Actions action){
			return add(action, false);
		}
		
		public Builder add(Actions action, boolean isJustPressed){
			data.triggers.add(new InputTrigger(action, isJustPressed));
			return this;
		}
		
		public InputTransitionData build(){
			return data;
		}
		
	}
}
