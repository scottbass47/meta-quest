package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.ai.AIController;

public class AIControllerComponent implements Component{

	public AIController controller;
	
	public AIControllerComponent(AIController controller){
		this.controller = controller;
	}
	
}
