package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.input.GameInput;

public class InputComponent implements Component{

	public GameInput input;
	
	public InputComponent(GameInput input){
		this.input = input;
	}
	
}
