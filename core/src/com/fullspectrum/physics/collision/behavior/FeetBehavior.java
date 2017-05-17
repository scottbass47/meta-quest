package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.BodyInfo;

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
