package com.fullspectrum.physics.collision;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.physics.FixtureType;

public class CollisionData {

	private ArrayMap<FixtureType, Array<CollisionListener>> listenerMap;
	private ArrayMap<FixtureType, ObjectSet<CollisionBodyType>> collisionMap;
	
	public CollisionData() {
		listenerMap = new ArrayMap<FixtureType, Array<CollisionListener>>();
		collisionMap = new ArrayMap<FixtureType, ObjectSet<CollisionBodyType>>();
	}
	
	public void registerDefault(FixtureType collision){
		add(collision, collision.getDefault(), collision.collidesWith());
	}
	
	private void add(FixtureType collision, CollisionListener listener, Array<CollisionBodyType> types){
		if(listener == null) return;
		if(!listenerMap.containsKey(collision)){
			listenerMap.put(collision, new Array<CollisionListener>());
			collisionMap.put(collision, new ObjectSet<CollisionBodyType>());
		}
		Array<CollisionListener> listeners = listenerMap.get(collision);
		listeners.add(listener);
		
		ObjectSet<CollisionBodyType> collisionTypes = collisionMap.get(collision);
		collisionTypes.addAll(types);
	}
	
	public ObjectSet<CollisionBodyType> getCollidesWith(FixtureType type){
		return collisionMap.get(type);
	}
	
	public Array<CollisionListener> getListeners(FixtureType type){
		return listenerMap.get(type);
	}
}