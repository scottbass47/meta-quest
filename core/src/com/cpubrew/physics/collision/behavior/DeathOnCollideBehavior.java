package com.cpubrew.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.cpubrew.component.Mappers;
import com.cpubrew.physics.collision.BodyInfo;

public class DeathOnCollideBehavior extends CollisionBehavior{

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		Mappers.death.get(me.getEntity()).triggerDeath();
	}

}
