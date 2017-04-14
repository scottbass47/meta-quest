package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.BoomerangAbility;
import com.fullspectrum.ability.BoomerangAbility.Phase;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.BodyInfo;

public class BoomerangBehavior implements CollisionBehavior {

	@Override
	public void preSolveCollision(BodyInfo me, BodyInfo other, Contact contact, Manifold manifold) {
		contact.setEnabled(false);
		BoomerangAbility boomerangAbility = (BoomerangAbility)Mappers.ability.get(other.getEntity()).getAbility(AbilityType.BOOMERANG);
		Phase phase = boomerangAbility.getCurrentPhase();
		if(phase == Phase.BACK){
			Mappers.death.get(me.getEntity()).triggerDeath();
			boomerangAbility.setDone(true);
		}
	}

	@Override
	public void postSolveCollision(BodyInfo me, BodyInfo other, Contact contact, ContactImpulse impulse) {
		
	}

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		
	}

	@Override
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		
	}
}
