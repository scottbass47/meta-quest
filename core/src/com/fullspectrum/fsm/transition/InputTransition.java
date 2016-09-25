package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;

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
			assert (inputComp != null);
			outerloop: 
			for (TransitionObject obj : machine.getCurrentState().getData(Transition.INPUT)) {
				InputTransitionData itd = (InputTransitionData) obj.data;
				if (itd == null) continue;
				boolean allPressed = true;
				String debug = itd.pressed ? "Pressed" : "Released";
				for (Actions trigger : itd.triggers) {
					if ((inputComp.input.getValue(trigger) > GameInput.ANALOG_THRESHOLD && itd.pressed)
							|| inputComp.input.getValue(trigger) < GameInput.ANALOG_THRESHOLD && !itd.pressed) {
						if (!itd.all) {
							itd.reset();
							System.out.println(machine + "-> Input " + debug);
//							System.out.println(itd);
							machine.changeState(machine.getCurrentState().getState(obj));
							break outerloop;
						}
					}
					else{
						if(itd.all){
							allPressed = false;
							break outerloop;
						}
					}
				}
				if(itd.all && allPressed){
					itd.reset();
					System.out.println(machine + "-> Input " + debug);
//					System.out.println(itd);
					machine.changeState(machine.getCurrentState().getState(obj));
				}
			}

		}
	}
}