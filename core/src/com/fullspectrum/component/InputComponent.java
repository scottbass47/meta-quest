package com.fullspectrum.component;

import com.fullspectrum.entity.Entity;
import com.fullspectrum.input.GameInput;

public abstract class InputComponent implements IComponent{

	protected GameInput input;
	protected PhysicsComponent physics;
	protected Entity entity;
	
	public InputComponent(GameInput input, PhysicsComponent physics){
		this.input = input;
		this.physics = physics;
	}
	
	public abstract void update(float delta, Entity entity);
	
	public GameInput getInput(){
		return input;
	}
}
