package com.cpubrew.physics.collision.exception;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.physics.collision.BodyInfo;
import com.cpubrew.physics.collision.behavior.CollisionBehavior;

public class ExceptionTable {

	private ArrayMap<CollisionException, Array<CollisionBehavior>> exceptionMap = new ArrayMap<CollisionException, Array<CollisionBehavior>>();
	
	public boolean hasException(BodyInfo b1, BodyInfo b2){
		Array<CollisionBehavior> behaviors = exceptionMap.get(new CollisionException(b1.getEntityType(), b1.getFixtureType(), b2.getEntityType()));
		if(behaviors == null) return false;
		return true;
	}
	
	public void addException(CollisionException exception, CollisionBehavior... behaviors){
		exceptionMap.put(exception, Array.with(behaviors));
	}
	
	public Array<CollisionBehavior> getBehaviors(BodyInfo b1, BodyInfo b2){
		return exceptionMap.get(new CollisionException(b1.getEntityType(), b1.getFixtureType(), b2.getEntityType()));
	}
	
}
