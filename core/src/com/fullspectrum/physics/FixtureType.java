package com.fullspectrum.physics;


import static com.fullspectrum.physics.collision.CollisionBodyType.ALL;
import static com.fullspectrum.physics.collision.CollisionBodyType.ITEM;
import static com.fullspectrum.physics.collision.CollisionBodyType.MOB;
import static com.fullspectrum.physics.collision.CollisionBodyType.PROJECTILE;
import static com.fullspectrum.physics.collision.CollisionBodyType.TILE;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;
import com.fullspectrum.physics.collision.NullCollisionListener;
import com.fullspectrum.physics.collision.listener.DropCollisionListener;
import com.fullspectrum.physics.collision.listener.ExplosiveParticleCollisionListener;
import com.fullspectrum.physics.collision.listener.FeetCollisionListener;
import com.fullspectrum.physics.collision.listener.LevelTriggerCollisionListener;
import com.fullspectrum.physics.collision.listener.ProjectileCollisionListener;
import com.fullspectrum.physics.collision.listener.TileCollisionListener;

public enum FixtureType {

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
	},
	HOMING_KNIFE {
		@Override
		public CollisionListener getListener() {
			return new CollisionListener() {
				@Override
				public void beginCollision(CollisionInfo info) {
					if(info.getOtherCollisionType() == CollisionBodyType.TILE){
						Mappers.death.get(info.getMe()).triggerDeath();
					}
				}
				@Override
				public void preSolveCollision(CollisionInfo info, Contact contact, Manifold manifold) {
					contact.setEnabled(false);
				}
				public void endCollision(CollisionInfo info) {}
				public void postSolveCollision(CollisionInfo info, Contact contact, ContactImpulse impulse) { }
			};
		}

		@Override
		public Array<CollisionBodyType> collidesWith() {
			return Array.with(TILE, MOB);
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
