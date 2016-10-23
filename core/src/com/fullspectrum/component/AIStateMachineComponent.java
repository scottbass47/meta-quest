package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.fsm.AIStateMachine;

public class AIStateMachineComponent implements Component{

	public AIStateMachine aism;
	
	public AIStateMachineComponent(AIStateMachine aism){
		this.aism = aism;
	}
}
