package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldCollision implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();

		Body b1 = f1.getBody();
		Body b2 = f2.getBody();

		Entity e1 = (Entity) b1.getUserData();
		Entity e2 = (Entity) b2.getUserData();

		if (f1.isSensor() && f1.getUserData() != null) {
			Sensors sensor = Sensors.get((String) f1.getUserData());
			sensor.beginCollision(f1, f2);
		}

		if (f2.isSensor() && f2.getUserData() != null) {
			Sensors sensor = Sensors.get((String) f2.getUserData());
			sensor.beginCollision(f2, f1);
		}

		if (!f1.isSensor() && f1.getUserData() != null) {
			Collisions collision = Collisions.get((String) f1.getUserData());
			if (collision != null) {
				collision.beginCollision(f1, f2);
			}
		}

		if (!f2.isSensor() && f2.getUserData() != null) {
			Collisions collision = Collisions.get((String) f2.getUserData());
			if (collision != null) {
				collision.beginCollision(f2, f1);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();

		Body b1 = f1.getBody();
		Body b2 = f2.getBody();

		Entity e1 = (Entity) b1.getUserData();
		Entity e2 = (Entity) b2.getUserData();

		if (f1.isSensor() && f1.getUserData() != null) {
			Sensors sensor = Sensors.get((String) f1.getUserData());
			sensor.endCollision(f1, f2);
		}

		if (f2.isSensor() && f2.getUserData() != null) {
			Sensors sensor = Sensors.get((String) f2.getUserData());
			sensor.endCollision(f2, f1);
		}

		if (!f1.isSensor() && f1.getUserData() != null) {
			Collisions collision = Collisions.get((String) f1.getUserData());
			if (collision != null) {
				collision.endCollision(f1, f2);
			}
		}

		if (!f2.isSensor() && f2.getUserData() != null) {
			Collisions collision = Collisions.get((String) f2.getUserData());
			if (collision != null) {
				collision.endCollision(f2, f1);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();

		Body b1 = f1.getBody();
		Body b2 = f2.getBody();

		Entity e1 = (Entity) b1.getUserData();
		Entity e2 = (Entity) b2.getUserData();

		if (!f1.isSensor() && f1.getUserData() != null) {
			Collisions collision = Collisions.get((String) f1.getUserData());
			if (collision != null) {
				collision.preSolve(f1, f2, contact);
			}
		}

		if (!f2.isSensor() && f2.getUserData() != null) {
			Collisions collision = Collisions.get((String) f2.getUserData());
			if (collision != null) {
				collision.preSolve(f2, f1, contact);
			}
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();

		Body b1 = f1.getBody();
		Body b2 = f2.getBody();

		Entity e1 = (Entity) b1.getUserData();
		Entity e2 = (Entity) b2.getUserData();

		if (!f1.isSensor() && f1.getUserData() != null) {
			Collisions collision = Collisions.get((String) f1.getUserData());
			if (collision != null) {
				collision.postSolve(f1, f2, contact);
			}
		}

		if (!f2.isSensor() && f2.getUserData() != null) {
			Collisions collision = Collisions.get((String) f2.getUserData());
			if (collision != null) {
				collision.postSolve(f2, f1, contact);
			}
		}
	}

}
