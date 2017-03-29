package com.fullspectrum.physics.collision.listener;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.DamageComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;

public class ContactDamageListener implements CollisionListener{

	@Override
	public void beginCollision(CollisionInfo info) {
		Entity entity = info.getMe();
		Entity otherEntity = info.getOther();
		
		HealthComponent healthComp = Mappers.heatlh.get(otherEntity);
		if(healthComp == null) return;
		
		// If entity is stunned, they won't have the damage component
		DamageComponent damageComp = Mappers.damage.get(entity);
		if(damageComp == null) return;
		
		DamageHandler.dealDamage(entity, otherEntity, damageComp.damage);
	}

	@Override
	public void endCollision(CollisionInfo info) {
	}

	@Override
	public void preSolveCollision(CollisionInfo info, Contact contact, Manifold manifold) {
		contact.setEnabled(false);
	}

	@Override
	public void postSolveCollision(CollisionInfo info, Contact contact, ContactImpulse impulse) {
	}

}
