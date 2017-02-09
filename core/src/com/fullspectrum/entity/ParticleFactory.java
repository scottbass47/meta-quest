package com.fullspectrum.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.level.Level;

public class ParticleFactory {

	public static void spawnJumpParticle(Entity entity){
		Engine engine = Mappers.engine.get(entity).engine;
		World world = Mappers.world.get(entity).world;
		Level level = Mappers.level.get(entity).level;
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		
		float yOff = bodyComp.getAABB().height * -0.5f + 0.35f;
		
		Entity particle = EntityFactory.createParticle(engine, world, level, Assets.getInstance().getSpriteAnimation(Assets.JUMP_PARTICLE), body.getPosition().x, body.getPosition().y + yOff);
		EntityManager.addEntity(particle);
	}
	
	
}
