package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.ai.PathFinder;

public class PathComponent implements Component{

	public PathFinder pathFinder;
	
	public PathComponent(PathFinder pathFinder){
		this.pathFinder = pathFinder;
	}
	
}
