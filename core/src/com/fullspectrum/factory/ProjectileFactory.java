package com.fullspectrum.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.game.GameVars;

public class ProjectileFactory {

	private ProjectileFactory() {
	}

	public static ProjectileData initProjectile(Entity entity, float xOff, float yOff, float angle) {
		BodyComponent bodyComp = Mappers.body.get(entity);
		FacingComponent facingComp = Mappers.facing.get(entity);

		Vector2 pos = bodyComp.body.getPosition();
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
		EngineComponent engineComp = Mappers.engine.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);
		ProjectileData data = initProjectile(entity, xOff, yOff, angle);

		EntityManager.addEntity(EntityFactory.createBullet(engineComp.engine, worldComp.world, levelComp.level, speed, data.angle, data.x, data.y, damage, false, Mappers.type.get(entity).type));
	}
	
	public static void spawnThrowingKnife(Entity entity, float xOff, float yOff, float speed, float damage, float angle) {
		EngineComponent engineComp = Mappers.engine.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);
		ProjectileData data = initProjectile(entity, xOff, yOff, angle);

		EntityManager.addEntity(EntityFactory.createThrowingKnife(engineComp.engine, worldComp.world, levelComp.level, speed, data.angle, data.x, data.y, damage, Mappers.type.get(entity).type));
	}

	public static void spawnExplosiveProjectile(Entity entity, float xOff, float yOff, float speed, float damage, float angle, float explosionRadius, float damageDropOffRate) {
		EngineComponent engineComp = Mappers.engine.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);
		ProjectileData data = initProjectile(entity, xOff, yOff, angle);

		EntityManager.addEntity(EntityFactory.createExplosiveProjectile(engineComp.engine, worldComp.world, levelComp.level, speed, data.angle, data.x, data.y, damage, true, Mappers.type.get(entity).type, explosionRadius, damageDropOffRate));
	}
	
	public static void spawnExplosiveParticle(Entity entity, float xOff, float yOff, float speed, float angle){
		EngineComponent engineComp = Mappers.engine.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);
		ProjectileData data = initProjectile(entity, xOff, yOff, angle);
		
		EntityManager.addEntity(EntityFactory.createExplosiveParticle(engineComp.engine, worldComp.world, levelComp.level, entity, speed, data.angle, data.x, data.y));
	}
	
	public static void spawnSpitProjectile(Entity entity, float xOff, float yOff, float speed, float damage, float angle, float airTime){
		EngineComponent engineComp = Mappers.engine.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);
		ProjectileData data = initProjectile(entity, xOff, yOff, angle);
		
		EntityManager.addEntity(EntityFactory.createSpitProjectile(engineComp.engine, worldComp.world, levelComp.level, speed, data.angle, data.x, data.y, damage, airTime, Mappers.type.get(entity).type));
	}
	
	private static class ProjectileData {

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
