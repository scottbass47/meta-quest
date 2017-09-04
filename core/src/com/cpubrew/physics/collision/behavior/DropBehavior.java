package com.cpubrew.physics.collision.behavior;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.cpubrew.audio.AudioLocator;
import com.cpubrew.audio.Sounds;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.MoneyComponent;
import com.cpubrew.physics.collision.BodyInfo;
import com.cpubrew.utils.PhysicsUtils;

public class DropBehavior extends CollisionBehavior{

	public DropBehavior() {
		preSolveType = PreSolveType.USE;
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
		
		Mappers.death.get(coin).triggerDeath();
		AudioLocator.getAudio().playSound(Sounds.COIN_PICKUP, PhysicsUtils.getPos(coin));
	}
}
