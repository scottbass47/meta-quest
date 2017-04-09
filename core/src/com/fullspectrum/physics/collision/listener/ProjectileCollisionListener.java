package com.fullspectrum.physics.collision.listener;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.BulletStatsComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;

public class ProjectileCollisionListener implements CollisionListener{

	@Override
	public void beginCollision(CollisionInfo info) {
		Entity entity = info.getMe();
		Entity otherEntity = info.getOther();
		
		if(info.getOtherCollisionType() == CollisionBodyType.TILE && Mappers.projectile.get(entity).tileCollisionOn){
			Mappers.death.get(entity).triggerDeath();
			return;
		}
		
		HealthComponent enemyHealth = Mappers.heatlh.get(otherEntity);
		if(enemyHealth == null) return;
		
		BulletStatsComponent bulletStatsComp = Mappers.bulletStats.get(entity);
		DamageHandler.dealDamage(entity, otherEntity, bulletStatsComp.damage);
		
		Mappers.death.get(entity).triggerDeath();
	}

	@Override
	public void endCollision(CollisionInfo info) {
		
	}

	@Override
	public void preSolveCollision(CollisionInfo info, Contact contact, Manifold manifold) {
		
	}

	@Override
	public void postSolveCollision(CollisionInfo info, Contact contact, ContactImpulse impulse) {
		
	}

}
