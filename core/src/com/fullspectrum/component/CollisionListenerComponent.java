package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionData;

public class CollisionListenerComponent implements Component, Poolable{

	public CollisionData collisionData;
	public CollisionBodyType type;
	
	@Override
	public void reset() {
		collisionData = null;
		type = null;
	}
}
