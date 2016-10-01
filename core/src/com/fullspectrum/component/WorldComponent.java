package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.World;

public class WorldComponent implements Component{

	public World world;
	
	public WorldComponent(World world){
		this.world = world;
	}
	
}
