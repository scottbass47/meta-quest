package com.fullspectrum.factory;

import com.fullspectrum.fsm.MultiTransition;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTransitionData.Type;
import com.fullspectrum.fsm.transition.Transitions;
import com.fullspectrum.input.Actions;

public class InputFactory {

	public static MultiTransition idle() {
		return new MultiTransition(Transitions.INPUT, idleNeither()).or(Transitions.INPUT, idleBoth());
	}
	
	public static InputTransitionData idleNeither() {
		return new InputTransitionData.Builder(Type.ALL, false).add(Actions.MOVE_RIGHT).add(Actions.MOVE_LEFT).build();
	}
	
	public static InputTransitionData idleBoth() {
		return new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVE_RIGHT).add(Actions.MOVE_LEFT).build();
	}
	
	public static InputTransitionData run() {
		return new InputTransitionData.Builder(Type.ONLY_ONE, true).add(Actions.MOVE_RIGHT).add(Actions.MOVE_LEFT).build();
	}
	
	public static InputTransitionData attack() {
		return new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build();
	}
	
	public static InputTransitionData jump() {
		return new InputTransitionData.Builder(Type.ALL, true).add(Actions.JUMP, true).build();
	}
}
