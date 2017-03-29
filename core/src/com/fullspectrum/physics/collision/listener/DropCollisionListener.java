package com.fullspectrum.physics.collision.listener;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;

public class DropCollisionListener implements CollisionListener{

	@Override
	public void beginCollision(CollisionInfo info) {
		
	}

	@Override
	public void endCollision(CollisionInfo info) {
		
	}

	@Override
	public void preSolveCollision(CollisionInfo info, Contact contact, Manifold manifold) {
		Entity coin = info.getMe();
		Entity entity = info.getOther();

		if(info.getOtherCollisionType() == CollisionBodyType.TILE) return;
		contact.setEnabled(false);
		
		if(Mappers.player.get(entity) == null || !Mappers.drop.get(coin).canPickUp) return;
		
		MoneyComponent moneyComp = Mappers.money.get(entity);
		MoneyComponent coinAmount = Mappers.money.get(coin);
		
		moneyComp.money += coinAmount.money;
		coinAmount.money = 0;
		
		Mappers.death.get(coin).triggerDeath();
	}

	@Override
	public void postSolveCollision(CollisionInfo info, Contact contact, ContactImpulse impulse) {
		
	}

}
