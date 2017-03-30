package com.fullspectrum.physics;


import static com.fullspectrum.physics.collision.CollisionBodyType.ALL;
import static com.fullspectrum.physics.collision.CollisionBodyType.ITEM;
import static com.fullspectrum.physics.collision.CollisionBodyType.MOB;
import static com.fullspectrum.physics.collision.CollisionBodyType.PROJECTILE;
import static com.fullspectrum.physics.collision.CollisionBodyType.TILE;

import com.badlogic.gdx.utils.Array;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionListener;
import com.fullspectrum.physics.collision.NullCollisionListener;
import com.fullspectrum.physics.collision.listener.DropCollisionListener;
import com.fullspectrum.physics.collision.listener.ExplosiveParticleCollisionListener;
import com.fullspectrum.physics.collision.listener.FeetCollisionListener;
import com.fullspectrum.physics.collision.listener.LevelTriggerCollisionListener;
import com.fullspectrum.physics.collision.listener.ProjectileCollisionListener;
import com.fullspectrum.physics.collision.listener.TileCollisionListener;

public enum FixtureType {

//	DROP {
//		@Override
//		public void beginCollision(Fixture me, Fixture other) {
//			
//		}
//
//		@Override
//		public void endCollision(Fixture me, Fixture other) {
//			
//		}
//
//		@Override
//		public void preSolve(Fixture me, Fixture other, Contact contact) {
//			Entity coin = (Entity)me.getBody().getUserData();
//			Entity entity = (Entity)other.getBody().getUserData();
//
//			if(entity == null || !EntityUtils.isValid(entity)) return;
//			contact.setEnabled(false);
//			
//			if(Mappers.player.get(entity) == null || !Mappers.drop.get(coin).canPickUp) return;
//			
//			MoneyComponent moneyComp = Mappers.money.get(entity);
//			MoneyComponent coinAmount = Mappers.money.get(coin);
//			
//			moneyComp.money += coinAmount.money;
//			coinAmount.money = 0;
//			
//			Mappers.death.get(coin).triggerDeath();
//		}
//
//		@Override
//		public void postSolve(Fixture me, Fixture other, Contact contact) {
//			
//		}
//	},
//	EXPLOSIVE{
//		@Override
//		public void beginCollision(Fixture me, Fixture other) {
//			Entity entity = (Entity)me.getBody().getUserData();
//			Entity otherEntity = (Entity)other.getBody().getUserData();
//			
//			TypeComponent myType = Mappers.type.get(entity);
//			TypeComponent otherType = otherEntity != null ? Mappers.type.get(otherEntity) : null;
//			
//			if(otherType != null && !myType.shouldCollide(otherType)) return;
//
//			if(!other.isSensor()){
//				CombustibleComponent combustibleComp = Mappers.combustible.get(entity);
//				combustibleComp.shouldExplode = true;
//				return;
//			}else if(other.isSensor()){
//				return;
//			}
//		}
//
//		@Override
//		public void endCollision(Fixture me, Fixture other) {
//			
//		}
//
//		@Override
//		public void preSolve(Fixture me, Fixture other, Contact contact) {
//			Entity entity = (Entity)me.getBody().getUserData();
//			Entity otherEntity = (Entity)other.getBody().getUserData();
//			if(otherEntity == null || !EntityUtils.isValid(otherEntity)) return;
//			
//			if(Mappers.type.get(entity).same(Mappers.type.get(otherEntity))){
//				contact.setEnabled(false);
//			}
//		}
//
//		@Override
//		public void postSolve(Fixture me, Fixture other, Contact contact) {
//			
//		}
//	},
//	// BUG Multiple fixtures on same entity can get triggered, damaging an entity more than it should
//	DAMAGE_ON_CONTACT {
//		@Override
//		public void beginCollision(Fixture me, Fixture other) {
//			Entity entity = (Entity)me.getBody().getUserData();
//			Entity otherEntity = (Entity)other.getBody().getUserData();
//			
//			TypeComponent myType = Mappers.type.get(entity);
//			TypeComponent otherType = otherEntity != null ? Mappers.type.get(otherEntity) : null;
//			
//			if(otherType == null || !myType.shouldCollide(otherType)) return;
//			
//			HealthComponent healthComp = Mappers.heatlh.get(otherEntity);
//			if(healthComp == null) return;
//			
//			// If entity is stunned, they won't have the damage component
//			DamageComponent damageComp = Mappers.damage.get(entity);
//			if(damageComp == null) return;
//			
//			DamageHandler.dealDamage(entity, otherEntity, damageComp.damage);
//		}
//
//		@Override
//		public void endCollision(Fixture me, Fixture other) {
//		}
//
//		@Override
//		public void preSolve(Fixture me, Fixture other, Contact contact) {
//			Entity otherEntity = (Entity)other.getBody().getUserData();
//			if(otherEntity == null || !EntityUtils.isValid(otherEntity)) return;
//			
//			contact.setEnabled(false);
//		}
//
//		@Override
//		public void postSolve(Fixture me, Fixture other, Contact contact) {
//		}
//	};
//	
//	
//	public abstract void beginCollision(Fixture me, Fixture other);
//	public abstract void endCollision(Fixture me, Fixture other);
//	public abstract void preSolve(Fixture me, Fixture other, Contact contact);
//	public abstract void postSolve(Fixture me, Fixture other, Contact contact);
//	
//	public static Collisions get(String name){
//		for(Collisions collision : Collisions.values()){
//			if(collision.name().equalsIgnoreCase(name)) return collision;
//		}
//		return null;
//	}
	
	BODY {
		@Override
		public CollisionListener getListener() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	GROUND {
		@Override
		public CollisionListener getListener() {
			return new TileCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(MOB, PROJECTILE, ITEM);
		}
	},
	BULLET{
		@Override
		public CollisionListener getListener() {
			return new ProjectileCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(MOB, TILE);
		}
	},
	DROP{
		@Override
		public CollisionListener getListener() {
			return new DropCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(TILE, MOB);
		}
	},
	EXPLOSIVE {
		@Override
		public CollisionListener getListener() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	EXPLOSIVE_PARTICLE {
		@Override
		public CollisionListener getListener() {
			return new ExplosiveParticleCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(TILE, MOB);
		}
	},
	LEVEL_TRIGGER {
		@Override
		public CollisionListener getListener() {
			return new LevelTriggerCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(MOB);
		}
	},
	FEET {
		@Override
		public CollisionListener getListener() {
			return new FeetCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(TILE);
		}
	},
	CONTACT_DAMAGE {
		@Override
		public CollisionListener getListener() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(MOB);
		}
	};
	
	
	public abstract CollisionListener getListener();
	public abstract Array<CollisionBodyType> collidesWith();
	
	public static FixtureType get(String name){
		for(FixtureType type : values()){
			if(type.name().equalsIgnoreCase(name)) return type;
		}
		return null;
	}
}
