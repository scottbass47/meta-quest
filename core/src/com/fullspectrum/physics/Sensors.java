package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fullspectrum.component.BulletStatsComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.CombustibleComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.LevelSwitchComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.ProjectileComponent;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.utils.EntityUtils;

public enum Sensors {

	FEET {
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			// TEMPORARY FIX, SHOULD ALLOW ENTITIES TO LAND ON OTHER THINGS OTHER THAN THE GROUND
			String data = (String)other.getUserData();
			if(data == null || !data.equals("ground")) return;
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.bottomContacts++;
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			// TEMPORARY FIX, SHOULD ALLOW ENTITIES TO LAND ON OTHER THINGS OTHER THAN THE GROUND
			String data = (String)other.getUserData();
			if(data == null || !data.equals("ground")) return;
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.bottomContacts--;			
		}
	},
//	SWORD{
//		@Override
//		public void beginCollision(Fixture me, Fixture other) {
//			Entity sword = (Entity)me.getBody().getUserData();
//			Entity otherEntity = (Entity)other.getBody().getUserData();
//			Entity swordWielder = Mappers.parent.get(sword).parent;
//			
//			if(!EntityUtils.isValid(otherEntity)) return;
//			
//			// Don't deal damage to an entity of the same type (e.g. no friendly fire)
//			if(!Mappers.type.get(swordWielder).shouldCollide(Mappers.type.get(otherEntity))) return;
//			
//			SwordStatsComponent swordStats = Mappers.swordStats.get(sword);
//			HealthComponent enemyHealth = Mappers.heatlh.get(otherEntity);
//			
//			if(enemyHealth != null && !swordStats.hitEntities.contains(otherEntity) && !otherEntity.equals(Mappers.parent.get(sword).parent)){
//				DamageHandler.dealDamage(otherEntity, swordStats.damage);
//				swordStats.hitEntities.add(otherEntity);
//			}
//		}
//
//		@Override
//		public void endCollision(Fixture me, Fixture other) {
//			Entity sword = (Entity)me.getBody().getUserData();
//			Entity otherEntity = (Entity)other.getBody().getUserData();
//			
//			SwordStatsComponent swordStats = Mappers.swordStats.get(sword);
//			swordStats.hitEntities.remove(otherEntity);
//		}
//	},
	LADDER{
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			Entity entity = (Entity)other.getBody().getUserData();
			if(entity == null || !EntityUtils.isValid(entity)) return;
			
			CollisionComponent collisionComp = Mappers.collision.get(entity);
			if(collisionComp == null) return;
			
			collisionComp.ladderContacts++;
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			Entity entity = (Entity)other.getBody().getUserData();
			if(entity == null || !EntityUtils.isValid(entity)) return;
			
			CollisionComponent collisionComp = Mappers.collision.get(entity);
			if(collisionComp == null) return;
			
			collisionComp.ladderContacts--;
		}
	},
	BULLET{
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			Entity entity = (Entity)me.getBody().getUserData();
			Entity otherEntity = (Entity)other.getBody().getUserData();
			
			if(otherEntity == null && !other.isSensor()){
				entity.add(new RemoveComponent());
				return;
			}else if(other.isSensor()){
				return;
			}
			HealthComponent enemyHealth = Mappers.heatlh.get(otherEntity);
			if(enemyHealth == null) return;
			
			// No damage dealt to entities of same type
			if(!Mappers.type.get(entity).shouldCollide(Mappers.type.get(otherEntity))) return;
			
			BulletStatsComponent bulletStatsComp = Mappers.bulletStats.get(entity);
			DamageHandler.dealDamage(entity, otherEntity, bulletStatsComp.damage);
			
			EntityManager.sendToDie(entity);
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			
		}
	},
	EXPLOSIVE_PARTICLE{
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			Entity entity = (Entity)me.getBody().getUserData();
			Entity otherEntity = (Entity)other.getBody().getUserData();
			
			if(otherEntity == null && !other.isSensor()){
				entity.add(new RemoveComponent());
				return;
			}else if(other.isSensor()){
				return;
			}
			HealthComponent enemyHealth = Mappers.heatlh.get(otherEntity);
			if(enemyHealth == null) return;
			
			// No damage dealt to entities of same type
			if(!Mappers.type.get(entity).shouldCollide(Mappers.type.get(otherEntity))) return;
			
			Entity explosion = Mappers.parent.get(entity).parent;
			if(explosion == null || !EntityUtils.isValid(explosion)) return;
			CombustibleComponent combustibleComp = Mappers.combustible.get(explosion);
			TimerComponent timerComp = Mappers.timer.get(entity);
			
			if(combustibleComp.hitEntities.contains(otherEntity)) return;
			combustibleComp.hitEntities.add(otherEntity);
			
			ProjectileComponent projectileComp = Mappers.projectile.get(entity);
			
			float speed = projectileComp.speed;
			float timeElapsed = timerComp.timers.get("particle_life").getElapsed();
			float distanceTraveled = speed * timeElapsed;
			
			// CLEANUP Knockback for mana bomb
			// BUG Direct hits don't give the right angle of knockback
			float knockBackDistance = combustibleComp.radius * 0.5f;
			
			DamageHandler.dealDamage(entity, otherEntity, MathUtils.clamp((int)(combustibleComp.damage - distanceTraveled * combustibleComp.dropOffRate), 1, Integer.MAX_VALUE), knockBackDistance, projectileComp.angle);
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			
		}
	},
	RIGHT_WALL{
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			// TEMPORARY FIX
			String data = (String)other.getUserData();
			if(data == null || !data.equals("ground")) return;
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.rightContacts++;			
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			// TEMPORARY FIX
			String data = (String)other.getUserData();
			if(data == null || !data.equals("ground")) return;
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.rightContacts--;
		}
	},
	LEFT_WALL{
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			// TEMPORARY FIX
			String data = (String)other.getUserData();
			if(data == null || !data.equals("ground")) return;
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.leftContacts++;
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			// TEMPORARY FIX
			String data = (String)other.getUserData();
			if(data == null || !data.equals("ground")) return;
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.leftContacts--;
		}
	},
	LEVEL_TRIGGER{
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			Entity entity = (Entity)me.getBody().getUserData();
			Entity player = (Entity)other.getBody().getUserData();
			if(!EntityUtils.isValid(player) || Mappers.player.get(player) == null) return;
			
			String levelSwitch = Mappers.levelSwitch.get(entity).data;
			player.add(Mappers.engine.get(player).engine.createComponent(LevelSwitchComponent.class).set(levelSwitch));
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			Entity player = (Entity)other.getBody().getUserData();
			if(!EntityUtils.isValid(player) || Mappers.player.get(player) == null) return;
			
			if(Mappers.levelSwitch.get(player) != null) player.remove(LevelSwitchComponent.class);
		}
	};
	
	public abstract void beginCollision(Fixture me, Fixture other);
	public abstract void endCollision(Fixture me, Fixture other);
	
	public static Sensors get(String name){
		for(Sensors sensor : Sensors.values()){
			if(sensor.name().equalsIgnoreCase(name)) return sensor;
		}
		return null;
	}
	
}
