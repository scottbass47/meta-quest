package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.physics.collision.CollisionBodyType;
import com.cpubrew.physics.collision.CollisionData;

public class CollisionListenerComponent implements Component, Poolable{

	public CollisionData collisionData;
	public CollisionBodyType type;
	
	@Override
	public void reset() {
		collisionData = null;
		type = null;
	}
}
