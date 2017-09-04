package com.cpubrew.factory;

import com.cpubrew.fsm.MultiTransition;
import com.cpubrew.fsm.transition.InputTransitionData;
import com.cpubrew.fsm.transition.Transitions;
import com.cpubrew.fsm.transition.InputTransitionData.Type;
import com.cpubrew.input.Actions;

public class InputFactory {

	public static MultiTransition idle() {
		return new MultiTransition(Transitions.INPUT, notAttack()).and(new MultiTransition(Transitions.INPUT, idleNeither()).or(Transitions.INPUT, idleBoth()));
	}
	
	public static InputTransitionData notAttack() {
		return new InputTransitionData.Builder(Type.ALL, false).add(Actions.ATTACK).build();
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
	
	public static InputTransitionData attack(Actions action) {
		return new InputTransitionData.Builder(Type.ALL, true).add(action, true).build();
	}
	
	public static InputTransitionData jump() {
		return new InputTransitionData.Builder(Type.ALL, true).add(Actions.JUMP, true).build();
	}
}
