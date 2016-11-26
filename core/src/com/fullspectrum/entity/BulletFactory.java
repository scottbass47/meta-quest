package com.fullspectrum.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.game.GameVars;

public class BulletFactory {
	
	private BulletFactory() {}
	
	/**
	 * Spawns a bullet relative to the entity using the entities direction. xOff and yOff should be the x offset and y offset in pixels.
	 * 
	 * @param entity
	 * @param xOff
	 * @param yOff
	 * @param speed
	 */
	public static void spawnBullet(Entity entity, float xOff, float yOff, float speed, float damage){
		FacingComponent facingComp = Mappers.facing.get(entity);
		EngineComponent engineComp = Mappers.engine.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);

		Vector2 pos = bodyComp.body.getPosition();
		xOff = GameVars.PPM_INV * xOff;
		yOff = GameVars.PPM_INV * yOff;
		float x = pos.x + (facingComp.facingRight ? xOff : -xOff);
		float y = pos.y + yOff;
		float angle = facingComp.facingRight ? 0 : 180;
		
		boolean friendly = Mappers.type.get(entity).type == EntityType.FRIENDLY;
		
		engineComp.engine.addEntity(EntityFactory.createBullet(engineComp.engine, worldComp.world, speed, angle, x , y, damage, false, friendly));
	}
}
