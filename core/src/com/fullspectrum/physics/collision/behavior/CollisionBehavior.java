package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.physics.collision.BodyInfo;

public abstract class CollisionBehavior {

	protected PreSolveType preSolveType = PreSolveType.DONT_USE;
	
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact){};
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact){};
	public void preSolveCollision(BodyInfo me, BodyInfo other, Contact contact, Manifold manifold){};
	public void postSolveCollision(BodyInfo me, BodyInfo other, Contact contact, ContactImpulse impulse){};

	public PreSolveType getPreSolveType() {
		return preSolveType;
	}
	
	public boolean shouldPreSolve() {
		return preSolveType == PreSolveType.USE;
	}
	
	public boolean shouldBeDisabled() {
		return preSolveType == PreSolveType.DISABLE_CONTACT;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public enum PreSolveType {
		USE,
		DONT_USE,
		DISABLE_CONTACT;
	}
}
