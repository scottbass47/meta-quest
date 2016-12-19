package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fullspectrum.component.BulletStatsComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.DropSpawnComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.SwordStatsComponent;
import com.fullspectrum.entity.DropType;
import com.fullspectrum.entity.EntityUtils;

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
	SWORD{
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			Entity sword = (Entity)me.getBody().getUserData();
			Entity otherEntity = (Entity)other.getBody().getUserData();
			Entity swordWielder = Mappers.parent.get(sword).parent;
			
			// Don't deal damage to an entity of the same type (e.g. no friendly fire)
			if(Mappers.type.get(swordWielder).type == Mappers.type.get(otherEntity).type) return;
			
			SwordStatsComponent swordStats = Mappers.swordStats.get(sword);
			HealthComponent enemyHealth = Mappers.heatlh.get(otherEntity);
			
			
			if(enemyHealth != null && !swordStats.hitEntities.contains(otherEntity, true) && !otherEntity.equals(Mappers.parent.get(sword).parent)){
				enemyHealth.health -= swordStats.damage;
				swordStats.hitEntities.add(otherEntity);
				
				if(enemyHealth.health <= 0/* && Mappers.type.get(otherEntity).type == EntityType.ENEMY*/){
					otherEntity.add(Mappers.engine.get(otherEntity).engine.createComponent(DropSpawnComponent.class).set(DropType.COIN));
				}
			}
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			Entity sword = (Entity)me.getBody().getUserData();
			Entity otherEntity = (Entity)other.getBody().getUserData();
			
			SwordStatsComponent swordStats = Mappers.swordStats.get(sword);
			swordStats.hitEntities.removeValue(otherEntity, true);
		}
	},
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
			if(Mappers.type.get(entity).type == Mappers.type.get(otherEntity).type) return;
			
			BulletStatsComponent bulletStatsComp = Mappers.bulletStats.get(entity);
			enemyHealth.health -= bulletStatsComp.damage;
		
			if(enemyHealth.health <= 0/* && Mappers.type.get(otherEntity).type == EntityType.ENEMY*/){
				otherEntity.add(Mappers.engine.get(otherEntity).engine.createComponent(DropSpawnComponent.class).set(DropType.COIN));
			}
			
			entity.add(new RemoveComponent());
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
