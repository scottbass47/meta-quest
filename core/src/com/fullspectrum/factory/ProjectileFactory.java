package com.fullspectrum.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.utils.PhysicsUtils;

public class ProjectileFactory {

	private ProjectileFactory() {
	}

	/** Offset are in pixels */
	public static ProjectileData initProjectile(Entity entity, float xOff, float yOff, float angle) {
		FacingComponent facingComp = Mappers.facing.get(entity);

		Vector2 pos = PhysicsUtils.getPos(entity);
		xOff = GameVars.PPM_INV * xOff;
		yOff = GameVars.PPM_INV * yOff;
		float x = pos.x + (facingComp == null || facingComp.facingRight ? xOff : -xOff);
		float y = pos.y + yOff;

		if (facingComp != null) {
			angle = facingComp.facingRight ? angle : 180 - angle;
		}

		return new ProjectileData(x, y, angle);
	}


	/**
	 * Spawns a bullet relative to the entity using the entities direction. xOff
	 * and yOff should be the x offset and y offset in pixels.
	 * 
	 * @param entity
	 * @param xOff
	 * @param yOff
	 * @param speed
	 */
	public static void spawnBullet(Entity entity, float xOff, float yOff, float speed, float damage) {
		spawnBullet(entity, xOff, yOff, speed, damage, 0);
	}

	public static void spawnBullet(Entity entity, float xOff, float yOff, float speed, float damage, float angle) {
		ProjectileData data = initProjectile(entity, xOff, yOff, angle);
		EntityManager.addEntity(EntityFactory.createBullet(speed, data.angle, data.x, data.y, damage, false, Mappers.status.get(entity).status));
	}
	
	public static void spawnThrowingKnife(Entity entity, float xOff, float yOff, float speed, float damage, float angle) {
		ProjectileData data = initProjectile(entity, xOff, yOff, angle);
		EntityManager.addEntity(EntityFactory.createThrowingKnife(data.x, data.y, speed, data.angle, damage, Mappers.status.get(entity).status));
	}
	
//	public static void spawnExplosiveProjectile(Entity entity, float xOff, float yOff, float speed, float damage, float angle, float explosionRadius, float damageDropOffRate) {
//		EngineComponent engineComp = Mappers.engine.get(entity);
//		WorldComponent worldComp = Mappers.world.get(entity);
//		LevelComponent levelComp = Mappers.level.get(entity);
//		ProjectileData data = initProjectile(entity, xOff, yOff, angle);
//
//		EntityManager.addEntity(EntityFactory.createExplosiveProjectile(engineComp.engine, worldComp.world, levelComp.level, speed, data.angle, data.x, data.y, damage, true, Mappers.type.get(entity).type, explosionRadius, damageDropOffRate));
//	}
	
	public static void spawnExplosiveParticle(Entity entity, float xOff, float yOff, float speed, float angle){
		ProjectileData data = initProjectile(entity, xOff, yOff, angle);
		EntityManager.addEntity(EntityFactory.createExplosiveParticle(entity, speed, data.angle, data.x, data.y));
	}
	
	public static class ProjectileData {

		public final float x;
		public final float y;
		public final float angle;

		public ProjectileData(float x, float y, float angle) {
			this.x = x;
			this.y = y;
			this.angle = angle;
		}

	}
}
