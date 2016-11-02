package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class DirectionComponent implements Component, Poolable{

	public enum Direction{
		LEFT,
		NONE,
		RIGHT;
		
		public int getDirection(){
			return ordinal() - 1;
		}
	}
	
	public Direction direction = Direction.NONE;

	@Override
	public void reset() {
		direction = Direction.NONE;
	}
	
}
