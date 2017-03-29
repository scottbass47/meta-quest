package com.fullspectrum.physics.collision.listener;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;

public class FeetCollisionListener implements CollisionListener{

	@Override
	public void beginCollision(CollisionInfo info) {
		CollisionComponent collisionComp = Mappers.collision.get(info.getMe());
		collisionComp.bottomContacts++;
	}

	@Override
	public void endCollision(CollisionInfo info) {
		CollisionComponent collisionComp = Mappers.collision.get(info.getMe());
		collisionComp.bottomContacts--;		
	}

	@Override
	public void preSolveCollision(CollisionInfo info, Contact contact, Manifold manifold) {
	}

	@Override
	public void postSolveCollision(CollisionInfo info, Contact contact, ContactImpulse impulse) {
	}

}
