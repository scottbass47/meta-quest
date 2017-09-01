package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.level.FlowField.Direction;
import com.fullspectrum.physics.collision.BodyInfo;

public class WallBehavior extends CollisionBehavior {

	private Direction direction;
	
	public WallBehavior(Direction direction) {
		this.direction = direction;
	}
	
	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		CollisionComponent collisionComp = Mappers.collision.get(me.getEntity());
		switch (direction) {
		case DOWN:
			collisionComp.bottomContacts++;
			break;
		case LEFT:
			collisionComp.leftContacts++;
			break;
		case RIGHT:
			collisionComp.rightContacts++;
			break;
		case UP:
			collisionComp.topContacts++;
			break;
		default:
			break;
		}
	}
	
	@Override
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		CollisionComponent collisionComp = Mappers.collision.get(me.getEntity());
		switch (direction) {
		case DOWN:
			collisionComp.bottomContacts--;
			break;
		case LEFT:
			collisionComp.leftContacts--;
			break;
		case RIGHT:
			collisionComp.rightContacts--;
			break;
		case UP:
			collisionComp.topContacts--;
			break;
		default:
			break;
		}
	}
	

}
