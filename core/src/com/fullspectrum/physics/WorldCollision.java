package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.physics.collision.BodyInfo;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionData;
import com.fullspectrum.physics.collision.FixtureInfo;
import com.fullspectrum.physics.collision.behavior.CollisionBehavior;
import com.fullspectrum.physics.collision.filter.CollisionFilter;

public class WorldCollision implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());
		
		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getType());
		
		// Handle exceptions
		for(CollisionFilter filter : info.getFilters()){
			if(filter.passesFilter(b1, b2)){
				for(CollisionBehavior behavior : info.getBehaviors(filter)){
					behavior.beginCollision(b1, b2, contact);
				}
			}
		}
		
		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getType());
		
		// Handle exceptions
		for(CollisionFilter filter : info.getFilters()){
			if(filter.passesFilter(b2, b1)){
				for(CollisionBehavior behavior : info.getBehaviors(filter)){
					behavior.beginCollision(b2, b1, contact);
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());
		
		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getType());
		
		// Handle exceptions
		for(CollisionFilter filter : info.getFilters()){
			if(filter.passesFilter(b1, b2)){
				for(CollisionBehavior behavior : info.getBehaviors(filter)){
					behavior.endCollision(b1, b2, contact);
				}
			}
		}
		
		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getType());
		
		// Handle exceptions
		for(CollisionFilter filter : info.getFilters()){
			if(filter.passesFilter(b2, b1)){
				for(CollisionBehavior behavior : info.getBehaviors(filter)){
					behavior.endCollision(b2, b1, contact);
				}
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());
		
		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getType());
		
		boolean disableContact = true;
		
		// Handle exceptions
		for(CollisionFilter filter : info.getFilters()){
			if(filter.passesFilter(b1, b2)){
				disableContact = false;
				for(CollisionBehavior behavior : info.getBehaviors(filter)){
					behavior.preSolveCollision(b1, b2, contact, oldManifold);
				}
			}
		}
		
		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getType());
		
		// Handle exceptions
		for(CollisionFilter filter : info.getFilters()){
			if(filter.passesFilter(b2, b1)){
				disableContact = false;
				for(CollisionBehavior behavior : info.getBehaviors(filter)){
					behavior.preSolveCollision(b2, b1, contact, oldManifold);
				}
			}
		}
		
		if(disableContact){
			contact.setEnabled(false);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());
		
		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getType());
		
		// Handle exceptions
		for(CollisionFilter filter : info.getFilters()){
			if(filter.passesFilter(b1, b2)){
				for(CollisionBehavior behavior : info.getBehaviors(filter)){
					behavior.postSolveCollision(b1, b2, contact, impulse);
				}
			}
		}
		
		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getType());
		
		// Handle exceptions
		for(CollisionFilter filter : info.getFilters()){
			if(filter.passesFilter(b2, b1)){
				for(CollisionBehavior behavior : info.getBehaviors(filter)){
					behavior.postSolveCollision(b2, b1, contact, impulse);
				}
			}
		}
	}
	
	private BodyInfo createBodyInfo(Fixture fixture){
		Body body = fixture.getBody();
		Entity entity = (Entity)body.getUserData();
		FixtureType type = (FixtureType) fixture.getUserData();
		EntityType entityType = Mappers.type.get(entity).type;
		CollisionBodyType bodyType = Mappers.collisionListener.get(entity).type;
		CollisionData data = Mappers.collisionListener.get(entity).collisionData;
		
		return new BodyInfo(entity, body, fixture, type, bodyType, entityType, data);
	}

//	@Override
//	public void beginContact(Contact contact) {
//		Fixture f1 = contact.getFixtureA();
//		Fixture f2 = contact.getFixtureB();
//		
//		Entity e1 = (Entity)f1.getBody().getUserData();
//		Entity e2 = (Entity)f2.getBody().getUserData();
//		
//		CollisionBodyType t1 = Mappers.collisionListener.get(e1).type;
//		CollisionBodyType t2 = Mappers.collisionListener.get(e2).type;
//		
//		// e1 collision with e2
//		Array<CollisionListener> listeners = getListeners(e1, e2, f1, f2);
//		if(listeners != null){
//			ContactInfo info = new ContactInfo(e1, e2, f1, f2, t2);
//			for(CollisionListener listener : listeners){
//				listener.beginCollision(info);
//			}
//		}
//		
//		// e2 collision with e1
//		listeners = getListeners(e2, e1, f2, f1);
//		if(listeners != null){
//			ContactInfo info = new ContactInfo(e2, e1, f2, f1, t1);
//			for(CollisionListener listener : listeners){
//				listener.beginCollision(info);
//			}
//		}
//	}
//
//	@Override
//	public void endContact(Contact contact) {
//		Fixture f1 = contact.getFixtureA();
//		Fixture f2 = contact.getFixtureB();
//		
//		Entity e1 = (Entity)f1.getBody().getUserData();
//		Entity e2 = (Entity)f2.getBody().getUserData();
//		
//		CollisionBodyType t1 = Mappers.collisionListener.get(e1).type;
//		CollisionBodyType t2 = Mappers.collisionListener.get(e2).type;
//		
//		// e1 collision with e2
//		Array<CollisionListener> listeners = getListeners(e1, e2, f1, f2);
//		if(listeners != null){
//			ContactInfo info = new ContactInfo(e1, e2, f1, f2, t2);
//			for(CollisionListener listener : listeners){
//				listener.endCollision(info);
//			}
//		}
//		
//		// e2 collision with e1
//		listeners = getListeners(e2, e1, f2, f1);
//		if(listeners != null){
//			ContactInfo info = new ContactInfo(e2, e1, f2, f1, t1);
//			for(CollisionListener listener : listeners){
//				listener.endCollision(info);
//			}
//		}
//	}
//
//	@Override
//	public void preSolve(Contact contact, Manifold oldManifold) {
//		Fixture f1 = contact.getFixtureA();
//		Fixture f2 = contact.getFixtureB();
//		
//		Entity e1 = (Entity)f1.getBody().getUserData();
//		Entity e2 = (Entity)f2.getBody().getUserData();
//		
//		CollisionBodyType t1 = Mappers.collisionListener.get(e1).type;
//		CollisionBodyType t2 = Mappers.collisionListener.get(e2).type;
//
//		boolean disableContact = true;
//		
//		// e1 collision with e2
//		Array<CollisionListener> listeners = getListeners(e1, e2, f1, f2);
//		if(listeners != null){
//			disableContact = false;
//			ContactInfo info = new ContactInfo(e1, e2, f1, f2, t2);
//			for(CollisionListener listener : listeners){
//				listener.preSolveCollision(info, contact, oldManifold);
//			}
//		}
//		
//		// e2 collision with e1
//		listeners = getListeners(e2, e1, f2, f1);
//		if(listeners != null){
//			disableContact = false;
//			ContactInfo info = new ContactInfo(e2, e1, f2, f1, t1);
//			for(CollisionListener listener : listeners){
//				listener.preSolveCollision(info, contact, oldManifold);
//			}
//		}
//		
//		if(disableContact){
//			contact.setEnabled(false);
//		}
//	}
//
//	// BUG Nullpointer exception on line 127
//	@Override
//	public void postSolve(Contact contact, ContactImpulse impulse) {
//		Fixture f1 = contact.getFixtureA();
//		Fixture f2 = contact.getFixtureB();
//		
//		Entity e1 = (Entity)f1.getBody().getUserData();
//		Entity e2 = (Entity)f2.getBody().getUserData();
//		
//		CollisionBodyType t1 = Mappers.collisionListener.get(e1).type;
//		CollisionBodyType t2 = Mappers.collisionListener.get(e2).type;
//		
//		// e1 collision with e2
//		Array<CollisionListener> listeners = getListeners(e1, e2, f1, f2);
//		if(listeners != null){
//			ContactInfo info = new ContactInfo(e1, e2, f1, f2, t2);
//			for(CollisionListener listener : listeners){
//				listener.postSolveCollision(info, contact, impulse);
//			}
//		}
//		
//		// e2 collision with e1
//		listeners = getListeners(e2, e1, f2, f1);
//		if(listeners != null){
//			ContactInfo info = new ContactInfo(e2, e1, f2, f1, t1);
//			for(CollisionListener listener : listeners){
//				listener.postSolveCollision(info, contact, impulse);
//			}
//		}
//	}
//
//	private Array<CollisionListener> getListeners(Entity e1, Entity e2, Fixture f1, Fixture f2){
//		CollisionListenerComponent listenerComp = Mappers.collisionListener.get(e1);
//		if(listenerComp != null){
//			if(shouldCollide(e1, e2, f1, f2)){
//				return listenerComp.collisionData.getListeners((FixtureType)f1.getUserData());
//			}
//		}
//		return null;
//	}
//	
//	/**
//	 * Returns whether or not e1 should collide with e2 
//	 * <br>ORDER MATTERS -> <code>shouldCollide(e1, e2) != shouldCollide(e2, e1)</code>
//	 * @param e1
//	 * @param e2
//	 * @return
//	 */
//	private boolean shouldCollide(Entity e1, Entity e2, Fixture f1, Fixture f2) {
//		CollisionListenerComponent listenerComp = Mappers.collisionListener.get(e1);
//		FixtureType fixtureType = (FixtureType)f1.getUserData();
//		
//		// Do the types match up
//		ObjectSet<CollisionBodyType> collide = listenerComp.collisionData.getCollidesWith(fixtureType);
//		if(!collide.contains(CollisionBodyType.ALL) && !collide.contains(Mappers.collisionListener.get(e2).type)) return false;
//		if(!typeCheck(e1, e2)) return false;
//		
//		return true;
//	}
//	
//	private boolean typeCheck(Entity e1, Entity e2){
//		TypeComponent typeComp1 = Mappers.type.get(e1);
//		TypeComponent typeComp2 = Mappers.type.get(e2);
//		if(typeComp1 == null) return true;
//		return typeComp1.shouldCollide(typeComp2);
//	}
}
