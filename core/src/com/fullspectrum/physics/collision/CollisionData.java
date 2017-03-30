package com.fullspectrum.physics.collision;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.physics.FixtureType;

public class CollisionData {

	private ArrayMap<FixtureType, Array<CollisionListener>> listenerMap;
	private ArrayMap<FixtureType, ObjectSet<CollisionBodyType>> collisionMap;
//	private ArrayMap<FixtureType, Boolean> lockedMap;
	
	public CollisionData() {
		listenerMap = new ArrayMap<FixtureType, Array<CollisionListener>>();
		collisionMap = new ArrayMap<FixtureType, ObjectSet<CollisionBodyType>>();
//		lockedMap = new ArrayMap<FixtureType, Boolean>();
	}
	
	public void registerDefault(FixtureType type){
		add(type, type.getListener(), type.collidesWith());
	}
	
	/**
	 * Clears all existing listeners for the specified fixture type and sets the new collision listener
	 * @param type
	 * @param listener
	 */
	public void setCollisionListener(FixtureType type, CollisionListener listener){
		clearListeners(type);
		add(type, listener, type.collidesWith());
	}
	
	/**
	 * Clears all existing listeners for the specified fixture type
	 * @param type
	 */
	public void clearListeners(FixtureType type){
		listenerMap.get(type).clear();
		collisionMap.get(type).clear();
	}
	
	public void addListener(FixtureType type, CollisionListener listener){
		add(type, listener, type.collidesWith());
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
	
//	public void lock(FixtureType type){
//		lockedMap.put(type, true);
//	}
//	
//	public void unlock(FixtureType type){
//		lockedMap.put(type, false);
//	}
//	
//	public boolean isLocked(FixtureType type){
//		return lockedMap.get(type);
//	}
}