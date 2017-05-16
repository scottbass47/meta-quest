package com.fullspectrum.physics.collision.behavior;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.audio.AudioLocator;
import com.fullspectrum.audio.Sounds;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.physics.collision.BodyInfo;
import com.fullspectrum.utils.PhysicsUtils;

public class DropBehavior implements CollisionBehavior{

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		
	}

	@Override
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		
	}

	@Override
	public void preSolveCollision(BodyInfo me, BodyInfo other, Contact contact, Manifold manifold) {
		Entity coin = me.getEntity();
		Entity player = other.getEntity();

		if(!Mappers.drop.get(coin).canPickUp) return;
		
		MoneyComponent moneyComp = Mappers.money.get(player);
		MoneyComponent coinAmount = Mappers.money.get(coin);
		
		moneyComp.money += coinAmount.money;
		coinAmount.money = 0;
		
		AudioLocator.getAudio().playSound(Sounds.COIN_PICKUP, PhysicsUtils.getPos(coin));
	}

	@Override
	public void postSolveCollision(BodyInfo me, BodyInfo other, Contact contact, ContactImpulse impulse) {
		
	}

}
