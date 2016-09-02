package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.player.Player;
import com.fullspectrum.fsm.EntityStateMachine;
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
		for (Entity e : entities) {
			FSMComponent fsmComp = Mappers.fsm.get(e);
			InputComponent inputComp = Mappers.input.get(e);
			assert (fsmComp != null && inputComp != null);
			EntityStateMachine fsm = fsmComp.fsm;
			outerloop:
			for(TransitionObject obj : fsm.getCurrentState().getData(Transition.INPUT)){
				InputTransitionData itd = (InputTransitionData) obj.data;
				if (itd == null) continue;
				for (Actions trigger : itd.triggers) {
					if (inputComp.input.getValue(trigger) > GameInput.ANALOG_THRESHOLD) {
						itd.reset();
						System.out.println("Input Pressed");
						System.out.println(itd);
						fsm.changeState(fsm.getCurrentState().getState(obj));
						break outerloop;
					}
				}
			}

		}
	}
}