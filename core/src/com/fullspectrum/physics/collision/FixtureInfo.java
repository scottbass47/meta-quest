package com.fullspectrum.physics.collision;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.physics.collision.behavior.CollisionBehavior;
import com.fullspectrum.physics.collision.filter.CollisionFilter;

public class FixtureInfo {

	private ArrayMap<CollisionFilter, Array<CollisionBehavior>> behaviorMap;
//	private Array<CollisionBehavior> preSolveBehaviors; // CLEANUP Is this needed?
	
	public FixtureInfo() {
		behaviorMap = new ArrayMap<CollisionFilter, Array<CollisionBehavior>>();
//		preSolveBehaviors = new Array<CollisionBehavior>();
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
//		if(behavior.shouldPreSolve() || behavior.shouldBeDisabled()) preSolveBehaviors.add(behavior);
	}
	
	public void removeBehaviors(CollisionFilter filter) {
		if(!behaviorMap.containsKey(filter)) return;
		behaviorMap.removeKey(filter);
	}
	
//	public boolean containsPreSolveBehavior() {
//		return preSolveBehaviors.size > 0;
//	}
	
	public void addBehaviors(CollisionFilter filter, CollisionBehavior... behaviors){
		for(CollisionBehavior behavior : behaviors) addBehavior(filter, behavior);
	}
	
}
