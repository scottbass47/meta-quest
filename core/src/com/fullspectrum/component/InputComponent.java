package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.input.Input;

public class InputComponent implements Component{

	public Input input;
	
	public InputComponent(Input input){
		this.input = input;
	}
	
}
