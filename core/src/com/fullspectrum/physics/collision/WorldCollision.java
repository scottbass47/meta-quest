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

// PERFORMANCE IMPORTANT Huge lag spikes with many enemies
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

		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getFixtureType());
		CollisionInstance instance = getCollisionInstance(b1, b2);

		boolean disableContact = true;

		if (!preSolveMap.containsKey(instance) || !preSolveMap.get(instance)) {
			// Handle exceptions
			if (exceptionTable.hasException(b1, b2)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b1, b2)) {
					behavior.preSolveCollision(b1, b2, contact, oldManifold);
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b1, b2)) {
						disableContact = false;
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							behavior.preSolveCollision(b1, b2, contact, oldManifold);
						}
					}
				}
			}
		}
		preSolveMap.put(instance, true);

		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getFixtureType());
		instance = getCollisionInstance(b2, b1);

		if (!preSolveMap.containsKey(instance) || !preSolveMap.get(instance)) {
			// Handle exceptions
			if (exceptionTable.hasException(b2, b1)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b2, b1)) {
					behavior.preSolveCollision(b2, b1, contact, oldManifold);
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b2, b1)) {
						disableContact = false;
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							behavior.preSolveCollision(b2, b1, contact, oldManifold);
						}
					}
				}
			}
		}
		preSolveMap.put(instance, true);

		if (disableContact) {
			contact.setEnabled(false);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());

		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getFixtureType());
		CollisionInstance instance = getCollisionInstance(b1, b2);

		if (!postSolveMap.containsKey(instance) || !postSolveMap.get(instance)) {
			// Handle exceptions
			if (exceptionTable.hasException(b1, b2)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b1, b2)) {
					behavior.postSolveCollision(b1, b2, contact, impulse);
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b1, b2)) {
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							behavior.postSolveCollision(b1, b2, contact, impulse);
						}
					}
				}
			}
		}
		postSolveMap.put(instance, true);

		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getFixtureType());
		instance = getCollisionInstance(b2, b1);

		if (!postSolveMap.containsKey(instance) || !postSolveMap.get(instance)) {
			// Handle exceptions
			if (exceptionTable.hasException(b2, b1)) {
				for (CollisionBehavior behavior : exceptionTable.getBehaviors(b2, b1)) {
					behavior.postSolveCollision(b2, b1, contact, impulse);
				}
			}
			else {
				for (CollisionFilter filter : info.getFilters()) {
					if (filter.passesFilter(b2, b1)) {
						for (CollisionBehavior behavior : info.getBehaviors(filter)) {
							behavior.postSolveCollision(b2, b1, contact, impulse);
						}
					}
				}
			}
		}
		postSolveMap.put(instance, true);
	}

	public void update() {
		preSolveMap.clear();
		postSolveMap.clear();
	}

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
