package com.fullspectrum.physics.collision.listener;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.CombustibleComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.ProjectileComponent;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.Maths;

public class ExplosiveParticleCollisionListener implements CollisionListener{

	@Override
	public void beginCollision(CollisionInfo info) {
		Entity projectile = info.getMe();
		Entity otherEntity = info.getOther();
		
		if(info.getOtherCollisionType() == CollisionBodyType.TILE){
			projectile.add(new RemoveComponent());
			return;
		}

		HealthComponent enemyHealth = Mappers.heatlh.get(otherEntity);
		if(enemyHealth == null) return;
		
		Entity explosion = Mappers.parent.get(projectile).parent;
		if(explosion == null || !EntityUtils.isValid(explosion)) return;
		
		CombustibleComponent combustibleComp = Mappers.combustible.get(explosion);
		TimerComponent timerComp = Mappers.timer.get(projectile);
		
		if(combustibleComp.hitEntities.contains(otherEntity)) return;
		combustibleComp.hitEntities.add(otherEntity);
		
		ProjectileComponent projectileComp = Mappers.projectile.get(projectile);
		
		float speed = projectileComp.speed;
		float timeElapsed = timerComp.timers.get("particle_life").getElapsed();
		float distanceTraveled = speed * timeElapsed;
		
		// CLEANUP Knockback for mana bomb
		float knockBackDistance = combustibleComp.radius * 0.5f;
		float angle = projectileComp.angle;
		
		// For direct hits, check the angle of the explosion relative to the hit entity
		if(distanceTraveled < 0.2f){
			angle = MathUtils.radDeg * Maths.atan2(otherEntity, projectile);
			switch(Maths.getQuad(angle)){
			case 1:
				angle = 0;
				break;
			case 2:
				angle = 180;
				break;
			case 3:
				angle = 181;
				break;
			case 4:
				angle = 359;
				break;
			}
		}
		DamageHandler.dealDamage(projectile, otherEntity, MathUtils.clamp((int)(combustibleComp.damage - distanceTraveled * combustibleComp.dropOffRate), 1, Integer.MAX_VALUE), knockBackDistance, angle);
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
