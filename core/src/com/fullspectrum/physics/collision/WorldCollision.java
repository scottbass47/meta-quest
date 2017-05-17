package com.fullspectrum.physics.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.debug.Time;
import com.fullspectrum.entity.EntityStatus;
import com.fullspectrum.entity.EntityType;
import com.fullspectrum.physics.FixtureType;
import com.fullspectrum.physics.collision.behavior.CollisionBehavior;
import com.fullspectrum.physics.collision.behavior.SensorBehavior;
import com.fullspectrum.physics.collision.exception.CollisionException;
import com.fullspectrum.physics.collision.exception.ExceptionTable;
import com.fullspectrum.physics.collision.filter.CollisionFilter;

// PERFORMANCE PreSolveMap and PostSolveMap clearing is much better now, but still some performance problems with a lot of enemies
public class WorldCollision implements ContactListener {

	private ExceptionTable exceptionTable;
	private ArrayMap<CollisionInstance, Integer> collisionMap;
	private ArrayMap<CollisionInstance, Boolean> preSolveMap;
	private ArrayMap<CollisionInstance, Boolean> postSolveMap;

	public WorldCollision() {
		exceptionTable = new ExceptionTable();
		collisionMap = new ArrayMap<CollisionInstance, Integer>();
		preSolveMap = new ArrayMap<CollisionInstance, Boolean>();
		postSolveMap = new ArrayMap<CollisionInstance, Boolean>();
		initExceptions();
	}

	private void initExceptions() {
		// Balloon trap colliding with itself
		CollisionException balloonTrapException = new CollisionException(EntityType.BALLOON_TRAP, FixtureType.BODY, EntityType.BALLOON_TRAP);
		exceptionTable.addException(balloonTrapException, new SensorBehavior());
	}

	@Override
	public void beginContact(Contact contact) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());

		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getFixtureType());
		CollisionInstance instance = getCollisionInstance(b1, b2);

		if (!shouldSkipCollision(instance)) {
			// Handle exceptions
			if (exceptionTable.hasException(b1, b2)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b1, b2)) {
					behavior.beginCollision(b1, b2, contact);
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b1, b2)) {
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							behavior.beginCollision(b1, b2, contact);
						}
					}
				}
			}
		}
		addToCollisionMap(instance);

		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getFixtureType());
		instance = getCollisionInstance(b2, b1);

		// Handle exceptions
		if (!shouldSkipCollision(instance)) {
			if (exceptionTable.hasException(b2, b1)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b2, b1)) {
					behavior.beginCollision(b2, b1, contact);
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b2, b1)) {
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							behavior.beginCollision(b2, b1, contact);
						}
					}
				}
			}
		}
		addToCollisionMap(instance);
	}

	@Override
	public void endContact(Contact contact) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());

		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getFixtureType());
		CollisionInstance instance = getCollisionInstance(b1, b2);
		subtractFromCollisionMap(instance);

		// Handle exceptions
		if (!shouldSkipCollision(instance)) {
			if (exceptionTable.hasException(b1, b2)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b1, b2)) {
					behavior.endCollision(b1, b2, contact);
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b1, b2)) {
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							behavior.endCollision(b1, b2, contact);
						}
					}
				}
			}
		}

		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getFixtureType());
		instance = getCollisionInstance(b2, b1);
		subtractFromCollisionMap(instance);

		// Handle exceptions
		if (!shouldSkipCollision(instance)) {
			if (exceptionTable.hasException(b2, b1)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b2, b1)) {
					behavior.endCollision(b2, b1, contact);
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b2, b1)) {
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							behavior.endCollision(b2, b1, contact);
						}
					}
				}
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());
		
		DisableType type1 = handlePreSolve(b1, b2, contact, oldManifold);
		DisableType type2 = handlePreSolve(b2, b1, contact, oldManifold);

		// If both types are inactive, then the collision should be disabled
		// If one of the types is active, then the collision should be disabled
		if((type1 == DisableType.INACTIVE && type2 == DisableType.INACTIVE) ||
			type1 == DisableType.ACTIVE || type2 == DisableType.ACTIVE) {
			contact.setEnabled(false);
		}
	}
	
	/**
	 * Returns true if the contact should be disabled. The contact should only be disabled if all 
	 * the behaviors tied to it have the <code>PreSolveType</code> DISABLE_CONTACT.
	 * 
	 * @param b1
	 * @param b2
	 * @param contact
	 * @param oldManifold
	 * @return
	 */
	private DisableType handlePreSolve(BodyInfo b1, BodyInfo b2, Contact contact, Manifold oldManifold) {
		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getFixtureType());
		
		CollisionInstance instance = getCollisionInstance(b1, b2);
		boolean inactiveCollision = true; // true if no filters are passed (i.e. b1 has no collision behaviors for b2)
		boolean disableContact = false; // true if b1 wants to disable the contact
		boolean saveInstance = false; // do we want to keep this collision instance in the pre-solve map?

		if (!preSolveMap.containsKey(instance) || !preSolveMap.get(instance)) {
			// Handle exceptions
			if (exceptionTable.hasException(b1, b2)) {
				inactiveCollision = false;
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b1, b2)) {
					disableContact = disableContact || behavior.shouldBeDisabled();
					if(behavior.shouldPreSolve()) {
						saveInstance = true;
						behavior.preSolveCollision(b1, b2, contact, oldManifold);
					}
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b1, b2)) {
						inactiveCollision = false; // If one filter is passed, then the collision is being handled. By default handled collision don't disable on contact
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							disableContact = disableContact || behavior.shouldBeDisabled();
							if(behavior.shouldPreSolve()) {
								saveInstance = true; // if one behavior uses pre-solve, then this collision instance must be saved
								behavior.preSolveCollision(b1, b2, contact, oldManifold);
							}
						}
					}
				}
			}
		}
		if(saveInstance) preSolveMap.put(instance, true);
		if(inactiveCollision) {
			return DisableType.INACTIVE;
		} else if(disableContact) {
			return DisableType.ACTIVE;
		} else {
			return DisableType.NONE;
		}
	}
	
	/**
	 * Represents what type of disabling a collision wants. <br><br>
	 * INACTIVE - collision has no behaviors associated with the collision <br>
	 * ACTIVE - collision has at least 1 behavior associated with the colliison with a <code>PreSolveType</code> of <code>DISABLE_CONTACT</code><br>
	 * NONE - collision has filters that are compatible with the body being collided with, but doesn't specify an active DISABLE_CONTACT
	 * 
	 * @author Scott
	 *
	 */
	private enum DisableType {
		INACTIVE,
		ACTIVE,
		NONE
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());

		// b1 as me
		handlePostSolve(b1, b2, contact, impulse);
		handlePostSolve(b2, b1, contact, impulse);
	}

	private void handlePostSolve(BodyInfo b1, BodyInfo b2, Contact contact, ContactImpulse impulse) {
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getFixtureType());
		
		CollisionInstance instance = getCollisionInstance(b1, b2);
		boolean saveInstance = false;

		if (!postSolveMap.containsKey(instance) || !postSolveMap.get(instance)) {
			// Handle exceptions
			if (exceptionTable.hasException(b1, b2)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b1, b2)) {
					if(behavior.shouldPreSolve()) {
						saveInstance = true;
						behavior.postSolveCollision(b1, b2, contact, impulse);
					}
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b1, b2)) {
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							if(behavior.shouldPreSolve()) {
								saveInstance = true;
								behavior.postSolveCollision(b1, b2, contact, impulse);
							}
						}
					}
				}
			}
		}
		if(saveInstance) postSolveMap.put(instance, true);
	}
	
	public void update() {
		Time.start("Clearing - " + preSolveMap.size);
		if(preSolveMap.size == 0) {
			Time.stop();
			return;
		}
		preSolveMap.clear();
		postSolveMap.clear();
		Time.stop();
	}
	
//	private boolean collisionBetween(EntityType t1, EntityType t2, BodyInfo b1, BodyInfo b2) {
//		return (b1.getEntityType() == t1 && b2.getEntityType() == t2) ||
//			   (b2.getEntityType() == t1 && b1.getEntityType() == t2);
//	}

	private BodyInfo createBodyInfo(Fixture fixture) {
		Body body = fixture.getBody();
		Entity entity = (Entity) body.getUserData();
		FixtureType type = (FixtureType) fixture.getUserData();
		EntityStatus entityStatus = Mappers.status.get(entity).status;
		EntityType entityType = Mappers.entity.get(entity).type;
		CollisionBodyType bodyType = Mappers.collisionListener.get(entity).type;
		CollisionData data = Mappers.collisionListener.get(entity).collisionData;

		return new BodyInfo(entity, body, fixture, type, bodyType, entityStatus, entityType, data);
	}

	private CollisionInstance getCollisionInstance(BodyInfo me, BodyInfo other) {
		return new CollisionInstance(me.getEntity(), me.getFixtureType(), other.getEntity());
	}

	private void addToCollisionMap(CollisionInstance instance) {
		if (!collisionMap.containsKey(instance)) {
			collisionMap.put(instance, 1);
		}
		else {
			collisionMap.put(instance, collisionMap.get(instance) + 1);
		}
	}

	private void subtractFromCollisionMap(CollisionInstance instance) {
		if (collisionMap.get(instance) == 1) {
			collisionMap.removeKey(instance);
		}
		else {
			collisionMap.put(instance, collisionMap.get(instance) - 1);
		}
	}

	private boolean shouldSkipCollision(CollisionInstance instance) {
		if (!collisionMap.containsKey(instance)) return false;
		return collisionMap.get(instance) > 0;
	}

}
