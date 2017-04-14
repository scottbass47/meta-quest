package com.fullspectrum.physics.collision;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.physics.collision.behavior.CollisionBehavior;
import com.fullspectrum.physics.collision.filter.CollisionFilter;

public class FixtureInfo {

	private ArrayMap<CollisionFilter, Array<CollisionBehavior>> behaviorMap;
	
	public FixtureInfo() {
		behaviorMap = new ArrayMap<CollisionFilter, Array<CollisionBehavior>>();
	}
	
	public Array<CollisionBehavior> getBehaviors(CollisionFilter filter){
		return behaviorMap.get(filter);
	}
	
	public Array<CollisionFilter> getFilters(){
		return behaviorMap.keys().toArray();
	}
	
	public void addBehavior(CollisionFilter filter, CollisionBehavior behavior){
		if(!behaviorMap.containsKey(filter)) {
			behaviorMap.put(filter, new Array<CollisionBehavior>());
		}
		behaviorMap.get(filter).add(behavior);
	}
	
	public void addBehaviors(CollisionFilter filter, CollisionBehavior... behaviors){
		for(CollisionBehavior behavior : behaviors) addBehavior(filter, behavior);
	}
	
}
