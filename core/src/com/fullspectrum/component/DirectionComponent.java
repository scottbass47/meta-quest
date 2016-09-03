package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class DirectionComponent implements Component{

	public enum Direction{
		LEFT,
		NONE,
		RIGHT;
		
		public int getDirection(){
			return ordinal() - 1;
		}
	}
	
	public Direction direction = Direction.NONE;
	
}
