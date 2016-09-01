package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.fsm.EntityStateMachine;

public class FSMComponent implements Component{

	public EntityStateMachine fsm;
	
	public FSMComponent(EntityStateMachine fsm){
		this.fsm = fsm;
	}
	
}
