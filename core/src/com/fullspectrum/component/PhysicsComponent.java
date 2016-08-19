package com.fullspectrum.component;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.entity.Entity;

public abstract class PhysicsComponent implements IComponent {

	// Setup Physics Vars
	protected World world;
	protected Body body;
	
	public PhysicsComponent(World world){
		this.world = world;
	}
	
	public abstract void update(float delta, Entity entity);
	
	public void setTargetVelocity(float dx, float dy){
		this.dx = dx;
		this.dy = dy;
	}
}
