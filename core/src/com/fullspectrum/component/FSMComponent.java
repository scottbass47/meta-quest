package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.fsm.FSM;

public class FSMComponent implements Component{

	public FSM fsm;
	
	public FSMComponent(FSM fsm){
		this.fsm = fsm;
	}
	
}
