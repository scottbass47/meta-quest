package com.fullspectrum.physics.collision.listener;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.BoomerangAbility;
import com.fullspectrum.ability.BoomerangAbility.Phase;
import com.fullspectrum.component.BulletStatsComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;

public class BoomerangCollisionListener implements CollisionListener {

	private Entity player;
	private ObjectSet<Entity> hitEntities;
	
	public BoomerangCollisionListener(Entity player) {
		this.player = player;
		hitEntities = new ObjectSet<Entity>();
	}
	
	@Override
	public void beginCollision(CollisionInfo info) {
	}

	@Override
	public void endCollision(CollisionInfo info) {
	}

	@Override
	public void preSolveCollision(CollisionInfo info, Contact contact, Manifold manifold) {
		contact.setEnabled(false);
		
		Entity boomerang = info.getMe();
		Entity hit = info.getOther();
		
		if(info.getOtherCollisionType() == CollisionBodyType.TILE) return;
		if(hitEntities.contains(hit)) return;
		
		HealthComponent healthComp = Mappers.heatlh.get(hit);
		if(healthComp == null) return;
		
		if(hit.equals(player)){
			BoomerangAbility boomerangAbility = (BoomerangAbility)Mappers.ability.get(player).getAbility(AbilityType.BOOMERANG);
			Phase phase = boomerangAbility.getCurrentPhase();
			if(phase == Phase.BACK){
				Mappers.death.get(boomerang).triggerDeath();
				boomerangAbility.setDone(true);
			}
			return;
		}

		BulletStatsComponent bulletStatsComp = Mappers.bulletStats.get(boomerang);
		DamageHandler.dealDamage(boomerang, hit, bulletStatsComp.damage);
		
		hitEntities.add(hit);
	}

	@Override
	public void postSolveCollision(CollisionInfo info, Contact contact, ContactImpulse impulse) {
		
	}
}
