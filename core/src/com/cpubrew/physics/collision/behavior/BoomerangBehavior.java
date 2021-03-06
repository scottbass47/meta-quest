package com.cpubrew.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.rogue.BoomerangAbility;
import com.cpubrew.ability.rogue.BoomerangAbility.Phase;
import com.cpubrew.component.Mappers;
import com.cpubrew.physics.collision.BodyInfo;

public class BoomerangBehavior extends CollisionBehavior {

	public BoomerangBehavior() {
		preSolveType = PreSolveType.USE;
	}
	
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
}
