package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.utils.PhysicsUtils;

public class BodyComponent implements Component{

	public Body body;
	private Rectangle aabb;
	
	public BodyComponent(Body body){
		this.body = body;
	}
	
	public BodyComponent(){
		this(null);
	}
	
	// CACHE PHYSICS BODY
	public Rectangle getAABB(){
		return aabb;
	}
	
	public void updateAABB(){
		aabb = PhysicsUtils.getAABB(body);
	}
	
}
