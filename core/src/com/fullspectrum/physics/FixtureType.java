package com.fullspectrum.physics;


import static com.fullspectrum.physics.collision.CollisionBodyType.*;

import com.badlogic.gdx.utils.Array;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionListener;
import com.fullspectrum.physics.collision.NullCollisionListener;

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
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	TILE {
		@Override
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	BULLET{
		@Override
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	DROP{
		@Override
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	EXPLOSIVE {
		@Override
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	EXPLOSIVE_PARTICLE {
		@Override
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	LEVEL_TRIGGER {
		@Override
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	FEET {
		@Override
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	},
	CONTACT_DAMAGE {
		@Override
		public CollisionListener getDefault() {
			return new NullCollisionListener();
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(ALL);
		}
	};
	
	
	public abstract CollisionListener getDefault();
	public abstract Array<CollisionBodyType> collidesWith();
	
	public static FixtureType get(String name){
		for(FixtureType type : values()){
			if(type.name().equalsIgnoreCase(name)) return type;
		}
		return null;
	}
}
