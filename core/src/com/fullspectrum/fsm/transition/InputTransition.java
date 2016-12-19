package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.input.Input;

public class InputTransition extends TransitionSystem {

	private static InputTransition instance;

	public static InputTransition getInstance() {
		if (instance == null) {
			instance = new InputTransition();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			Entity e = machine.getEntity();
			InputComponent inputComp = Mappers.input.get(e);
			assert inputComp != null : "InputComponent can't be null.";
			if(!inputComp.enabled) continue;
			for (TransitionObject obj : machine.getCurrentState().getData(Transition.INPUT)) {
				InputTransitionData itd = (InputTransitionData) obj.data;
				if (itd == null) continue;
				if (checkInput(itd, inputComp.input)) {
//					String debug = itd.pressed ? "Pressed" : "Released";
//					System.out.println(machine + "-> Input " + debug);
					if(machine.changeState(obj)) break;
				}
			}
		}
	}

	private boolean checkInput(InputTransitionData itd, Input input) {
		int counter = 0;
		for (InputTrigger trigger : itd.triggers) {
			boolean triggered = false;
			// If its a game input, it must be past the analog threshold to be considered an action
			if(input instanceof GameInput){
				triggered = trigger.justPressed ? input.isJustPressed(trigger.action) : input.getValue(trigger.action) > GameInput.ANALOG_THRESHOLD;
			}
			else{
				triggered = trigger.justPressed ? input.isJustPressed(trigger.action) : input.isPressed(trigger.action);
			}
			triggered = (triggered && itd.pressed) || (!triggered && !itd.pressed);
			if (triggered && itd.type == InputTransitionData.Type.ANY_ONE) return true;
			if (triggered) counter++;
		}
		switch (itd.type) {
		case ANY_ONE:
			return false;
		case ONLY_ONE:
			return counter == 1;
		case ALL:
			return counter == itd.triggers.size;
		default:
			return false;
		}
	}
}