package com.fullspectrum.physics;


import static com.fullspectrum.physics.collision.CollisionBodyType.MOB;
import static com.fullspectrum.physics.collision.CollisionBodyType.SENSOR;
import static com.fullspectrum.physics.collision.CollisionBodyType.TILE;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.FixtureInfo;
import com.fullspectrum.physics.collision.behavior.DamageOnCollideBehavior;
import com.fullspectrum.physics.collision.behavior.DeathOnCollideBehavior;
import com.fullspectrum.physics.collision.behavior.DropBehavior;
import com.fullspectrum.physics.collision.behavior.ExplosiveParticleBehavior;
import com.fullspectrum.physics.collision.behavior.FeetBehavior;
import com.fullspectrum.physics.collision.behavior.LevelTriggerBehavior;
import com.fullspectrum.physics.collision.behavior.SensorBehavior;
import com.fullspectrum.physics.collision.behavior.SolidBehavior;
import com.fullspectrum.physics.collision.behavior.WindParticleBehavior;
import com.fullspectrum.physics.collision.filter.CollisionFilter;
import com.fullspectrum.physics.collision.filter.PlayerFilter;

public enum FixtureType {

	BODY {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			CollisionFilter filter = new CollisionFilter.Builder()
					.addBodyTypes(MOB)
					.allEntityTypes()
					.build();
			
			info.addBehavior(filter, new SensorBehavior());
			return info;
		}
	},
	GROUND {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			CollisionFilter filter = new CollisionFilter.Builder()
					.allBodyTypes()
					.removeBodyType(SENSOR)
					.allEntityTypes()
					.build();
			
			info.addBehavior(filter, new SolidBehavior());
			return info;
		}
	},
	BULLET{
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			// Mob Collision
			CollisionFilter filter = new CollisionFilter.Builder()
					.addBodyTypes(MOB)
					.allEntityTypes()
					.removeEntityType(Mappers.status.get(entity).status)
					.build();
			
			info.addBehaviors(filter, 
					new DeathOnCollideBehavior(),
					new DamageOnCollideBehavior());
			
			// Tile Collision
			filter = new CollisionFilter.Builder()
					.addBodyTypes(TILE)
					.allEntityTypes()
					.build();
			
			info.addBehavior(filter, new DeathOnCollideBehavior());
			
			return info;
		}
	},
	DROP {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			CollisionFilter filter = new CollisionFilter.Builder()
					.addCustomFilter(new PlayerFilter())
					.build();
			
			info.addBehaviors(filter, 
					new SensorBehavior(),
					new DropBehavior());
					
			return info;
		}
	},
	EXPLOSIVE_PARTICLE {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			// Mob Collision
			CollisionFilter filter = new CollisionFilter.Builder()
					.addBodyTypes(MOB)
					.allEntityTypes()
					.removeEntityType(Mappers.status.get(entity).status)
					.build();
			
			info.addBehaviors(filter, 
					new ExplosiveParticleBehavior(),
					new SensorBehavior());

			// Tile Collision
			filter = new CollisionFilter.Builder()
					.addBodyTypes(TILE)
					.allEntityTypes()
					.build();
			
			info.addBehavior(filter, new DeathOnCollideBehavior());
			
			return info;
		}
	},
	WIND_PARTICLE {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			// Mob Collision
			CollisionFilter filter = new CollisionFilter.Builder()
					.addBodyTypes(MOB)
					.allEntityTypes()
					.removeEntityType(Mappers.status.get(entity).status)
					.build();
			
			info.addBehaviors(filter, 
					new WindParticleBehavior(),
					new SensorBehavior());

			// Tile Collision
			filter = new CollisionFilter.Builder()
					.addBodyTypes(TILE)
					.allEntityTypes()
					.build();
			
			info.addBehavior(filter, new DeathOnCollideBehavior());
			
			return info;
		}
	},
	LEVEL_TRIGGER {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			CollisionFilter filter = new CollisionFilter.Builder()
					.addCustomFilter(new PlayerFilter())
					.build();
			
			info.addBehavior(filter, new LevelTriggerBehavior());
			
			return info;
		}
	},
	FEET {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			CollisionFilter filter = new CollisionFilter.Builder()
					.addBodyTypes(TILE)
					.allEntityTypes()
					.build();
			
			info.addBehavior(filter, new FeetBehavior());
			
			return info;
		}
	},
	CONTACT_DAMAGE {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			CollisionFilter filter = new CollisionFilter.Builder()
					.addBodyTypes(MOB)
					.addEntityTypes(Mappers.status.get(entity).status.getOpposite())
					.build();
			
			info.addBehavior(filter, new DamageOnCollideBehavior());
			
			return info;
		}
	},
	ROLL {
		@Override
		public FixtureInfo getDefaultInfo(Entity entity) {
			FixtureInfo info = new FixtureInfo();
			
			CollisionFilter filter = new CollisionFilter.Builder()
					.addBodyTypes(TILE)
					.allEntityTypes()
					.build();
			
			info.addBehavior(filter, new SolidBehavior());
			
			return info;
		}
	};
	
	public abstract FixtureInfo getDefaultInfo(Entity entity);
	
	public static FixtureType get(String name){
		for(FixtureType type : values()){
			if(type.name().equalsIgnoreCase(name)) return type;
		}
		return null;
	}
}
