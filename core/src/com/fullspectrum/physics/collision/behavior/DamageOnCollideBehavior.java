package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.physics.collision.BodyInfo;

public class DamageOnCollideBehavior extends CollisionBehavior{

	public DamageOnCollideBehavior() {
		preSolveType = PreSolveType.USE;
	}
	
	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
	}
	
	@Override
	public void preSolveCollision(BodyInfo me, BodyInfo other, Contact contact, Manifold manifold) {
		contact.setEnabled(false);
		DamageHandler.dealDamage(me.getEntity(), other.getEntity(), Mappers.damage.get(me.getEntity()) == null ? 0.0f : Mappers.damage.get(me.getEntity()).damage);
	}
}
