package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.BodyInfo;

public class DeathOnCollideBehavior extends CollisionBehavior{

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		Mappers.death.get(me.getEntity()).triggerDeath();
	}

}
