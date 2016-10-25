package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.level.Level;

public class LevelComponent implements Component{

	public Level level;
	
	public LevelComponent(Level level){
		this.level = level;
	}
	
}
