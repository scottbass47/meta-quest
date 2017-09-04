package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.input.Input;

public class InputComponent implements Component, Poolable{

	public Input input;
	public boolean enabled = true;

	@Override
	public void reset() {
		input = null;
		enabled = true;
	}
	
	public InputComponent set(Input input){
		this.input = input;
		return this;
	}
	
}
