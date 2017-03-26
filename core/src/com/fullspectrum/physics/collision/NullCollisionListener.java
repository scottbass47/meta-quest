package com.fullspectrum.physics.collision;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public class NullCollisionListener implements CollisionListener{

	public void beginCollision(CollisionInfo info) {}
	public void endCollision(CollisionInfo info) {}
	public void preSolveCollision(CollisionInfo info, Contact contact, Manifold manifold) {}
	public void postSolveCollision(CollisionInfo info, Contact contact, ContactImpulse impulse) {}

}
