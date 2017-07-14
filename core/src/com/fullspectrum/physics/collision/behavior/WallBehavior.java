package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.BodyInfo;

public class WallBehavior extends CollisionBehavior{

	private boolean right;
	
	public WallBehavior(boolean right) {
		this.right = right;
	}
	
	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		CollisionComponent collisionComp = Mappers.collision.get(me.getEntity());
		if(right) collisionComp.rightContacts++;
		else collisionComp.leftContacts++;
	}
	
	@Override
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		CollisionComponent collisionComp = Mappers.collision.get(me.getEntity());
		if(right) collisionComp.rightContacts--;
		else collisionComp.leftContacts--;
	}
	
}
