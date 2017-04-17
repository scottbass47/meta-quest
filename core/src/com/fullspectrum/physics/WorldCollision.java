package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityStatus;
import com.fullspectrum.entity.EntityType;
import com.fullspectrum.physics.collision.BodyInfo;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionData;
import com.fullspectrum.physics.collision.FixtureInfo;
import com.fullspectrum.physics.collision.behavior.CollisionBehavior;
import com.fullspectrum.physics.collision.behavior.SensorBehavior;
import com.fullspectrum.physics.collision.exception.CollisionException;
import com.fullspectrum.physics.collision.exception.ExceptionTable;
import com.fullspectrum.physics.collision.filter.CollisionFilter;

// BUG Piercing bullets will do damage multiple times to one entity
public class WorldCollision implements ContactListener {

	private ExceptionTable exceptionTable;

	public WorldCollision() {
		exceptionTable = new ExceptionTable();
		initExceptions();
	}
	
	private void initExceptions(){
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

		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getFixtureType());

		// Handle exceptions
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

	@Override
	public void endContact(Contact contact) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());

		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getFixtureType());

		// Handle exceptions
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

		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getFixtureType());

		// Handle exceptions
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

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		BodyInfo b1 = createBodyInfo(contact.getFixtureA());
		BodyInfo b2 = createBodyInfo(contact.getFixtureB());

		// b1 as me
		CollisionData data = b1.getData();
		FixtureInfo info = data.getFixtureInfo(b1.getFixtureType());

		boolean disableContact = true;

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

		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getFixtureType());

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

		// b2 as me
		data = b2.getData();
		info = data.getFixtureInfo(b2.getFixtureType());

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
}
