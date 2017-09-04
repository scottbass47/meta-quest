package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.utils.PhysicsUtils;

public class BodyComponent implements Component, Poolable {

	public Body body;
	private Rectangle aabb;

	// CACHE PHYSICS BODY
	public Rectangle getAABB() {
		return aabb;
	}

	public void updateAABB() {
		aabb = PhysicsUtils.getAABB(body);
	}

	public BodyComponent set(Body body) {
		if (body == null) return this;
		this.body = body;
		updateAABB();
		return this;
	}
	
	@Override
	public void reset() {
		body = null;
		aabb = null;
	}

}
