package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.movement.Movement;

public class ControlledMovementComponent implements Component, Poolable{

	private Array<Movement> movements;
	private int index;
	public float elapsed;
	
	public ControlledMovementComponent() {
		movements = new Array<Movement>();
		index = 0;
	}
	
	public ControlledMovementComponent set(Movement movement){
		this.movements.add(movement);
		return this;
	}
	
	public void changeMovement(int index){
		if(index < 0 || index >= movements.size) throw new IndexOutOfBoundsException();
		this.index = index;
		elapsed = 0;
	}
	
	public ControlledMovementComponent add(Movement movement){
		movements.add(movement);
		return this;
	}
	
	public ControlledMovementComponent addAll(Movement... movements){
		this.movements.addAll(movements);
		return this;
	}
	
	public Movement getCurrentMovement(){
		if(movements.size == 0) return null;
		return movements.get(index);
	}
	
	@Override
	public void reset() {
		movements = null;
		index = 0;
		elapsed = 0.0f;
	}
}
