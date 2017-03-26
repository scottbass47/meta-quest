package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.component.CollisionListenerComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;

public class WorldCollision implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();
		
		Entity e1 = (Entity)f1.getBody().getUserData();
		Entity e2 = (Entity)f2.getBody().getUserData();
		
		// e1 collision with e2
		Array<CollisionListener> listeners = getListeners(e1, e2, f1, f2);
		if(listeners != null){
			CollisionInfo info = new CollisionInfo(e1, e2, f1, f2);
			for(CollisionListener listener : listeners){
				listener.beginCollision(info);
			}
		}
		
		// e2 collision with e1
		listeners = getListeners(e2, e1, f2, f1);
		if(listeners != null){
			CollisionInfo info = new CollisionInfo(e2, e1, f2, f1);
			for(CollisionListener listener : listeners){
				listener.beginCollision(info);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();
		
		Entity e1 = (Entity)f1.getBody().getUserData();
		Entity e2 = (Entity)f2.getBody().getUserData();
		
		// e1 collision with e2
		Array<CollisionListener> listeners = getListeners(e1, e2, f1, f2);
		if(listeners != null){
			CollisionInfo info = new CollisionInfo(e1, e2, f1, f2);
			for(CollisionListener listener : listeners){
				listener.endCollision(info);
			}
		}
		
		// e2 collision with e1
		listeners = getListeners(e2, e1, f2, f1);
		if(listeners != null){
			CollisionInfo info = new CollisionInfo(e2, e1, f2, f1);
			for(CollisionListener listener : listeners){
				listener.endCollision(info);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();
		
		Entity e1 = (Entity)f1.getBody().getUserData();
		Entity e2 = (Entity)f2.getBody().getUserData();
		
		// e1 collision with e2
		Array<CollisionListener> listeners = getListeners(e1, e2, f1, f2);
		if(listeners != null){
			CollisionInfo info = new CollisionInfo(e1, e2, f1, f2);
			for(CollisionListener listener : listeners){
				listener.preSolveCollision(info, contact, oldManifold);
			}
		}
		
		// e2 collision with e1
		listeners = getListeners(e2, e1, f2, f1);
		if(listeners != null){
			CollisionInfo info = new CollisionInfo(e2, e1, f2, f1);
			for(CollisionListener listener : listeners){
				listener.preSolveCollision(info, contact, oldManifold);
			}
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();
		
		Entity e1 = (Entity)f1.getBody().getUserData();
		Entity e2 = (Entity)f2.getBody().getUserData();
		
		// e1 collision with e2
		Array<CollisionListener> listeners = getListeners(e1, e2, f1, f2);
		if(listeners != null){
			CollisionInfo info = new CollisionInfo(e1, e2, f1, f2);
			for(CollisionListener listener : listeners){
				listener.postSolveCollision(info, contact, impulse);
			}
		}
		
		// e2 collision with e1
		listeners = getListeners(e2, e1, f2, f1);
		if(listeners != null){
			CollisionInfo info = new CollisionInfo(e2, e1, f2, f1);
			for(CollisionListener listener : listeners){
				listener.postSolveCollision(info, contact, impulse);
			}
		}
	}

	private Array<CollisionListener> getListeners(Entity e1, Entity e2, Fixture f1, Fixture f2){
		CollisionListenerComponent listenerComp = Mappers.collisionListener.get(e1);
		if(listenerComp != null){
			if(shouldCollide(e1, e2, f1, f2)){
				return listenerComp.collisionData.getListeners((FixtureType)f1.getUserData());
			}
		}
		return null;
	}
	
	/**
	 * Returns whether or not e1 should collide with e2 
	 * <br>ORDER MATTERS -> <code>shouldCollide(e1, e2) != shouldCollide(e2, e1)</code>
	 * @param e1
	 * @param e2
	 * @return
	 */
	private boolean shouldCollide(Entity e1, Entity e2, Fixture f1, Fixture f2) {
		CollisionListenerComponent listenerComp = Mappers.collisionListener.get(e1);
		FixtureType fixtureType = (FixtureType)f1.getUserData();
		
		// Do the types match up
		ObjectSet<CollisionBodyType> collide = listenerComp.collisionData.getCollidesWith(fixtureType);
		if(!collide.contains(CollisionBodyType.ALL) && !collide.contains(Mappers.collisionListener.get(e2).type)) return false;
		if(!typeCheck(e1, e2)) return false;
		
		return true;
	}
	
	private boolean typeCheck(Entity e1, Entity e2){
		TypeComponent typeComp1 = Mappers.type.get(e1);
		TypeComponent typeComp2 = Mappers.type.get(e2);
		if(typeComp1 == null) return true;
		return typeComp1.shouldCollide(typeComp2);
	}
}
