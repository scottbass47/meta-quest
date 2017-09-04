package com.cpubrew.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.cpubrew.component.Mappers;
import com.cpubrew.physics.collision.BodyInfo;

public class FeetBehavior extends CollisionBehavior{

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		Mappers.collision.get(me.getEntity()).bottomContacts++;
	}

	@Override
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		Mappers.collision.get(me.getEntity()).bottomContacts--;
	}

}
