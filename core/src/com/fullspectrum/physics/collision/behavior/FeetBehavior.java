package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.BodyInfo;

public class FeetBehavior implements CollisionBehavior{

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		Mappers.collision.get(me.getEntity()).bottomContacts++;
	}

	@Override
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		Mappers.collision.get(me.getEntity()).bottomContacts--;
	}

	@Override
	public void preSolveCollision(BodyInfo me, BodyInfo other, Contact contact, Manifold manifold) {
		
	}

	@Override
	public void postSolveCollision(BodyInfo me, BodyInfo other, Contact contact, ContactImpulse impulse) {
		
	}
	
}
