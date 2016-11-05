package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SwordStatsComponent;
import com.fullspectrum.component.TypeComponent;

public enum Sensors {

	FEET {
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.bottomContacts++;			
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
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
			
			if(Mappers.type.get(swordWielder).type == Mappers.type.get(otherEntity).type) return;
			
			SwordStatsComponent swordStats = Mappers.swordStats.get(sword);
			HealthComponent enemyHealth = Mappers.heatlh.get(otherEntity);
			
			
			if(enemyHealth != null && !swordStats.hitEntities.contains(otherEntity, true) && !otherEntity.equals(Mappers.parent.get(sword).parent)){
				enemyHealth.health -= swordStats.damage;
				swordStats.hitEntities.add(otherEntity);
			}
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			Entity sword = (Entity)me.getBody().getUserData();
			Entity otherEntity = (Entity)other.getBody().getUserData();
			
			SwordStatsComponent swordStats = Mappers.swordStats.get(sword);
			swordStats.hitEntities.removeValue(otherEntity, true);
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
